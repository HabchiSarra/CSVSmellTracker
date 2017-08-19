package org.tandoori.metrics.calculator.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tandoori.metrics.calculator.DevelopersHandler;
import org.tandoori.metrics.calculator.SmellCode;
import org.tandoori.metrics.calculator.processing.verification.SmellChecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by sarra on 18/07/17.
 */
class SmellProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SmellProcessor.class.getName());

    private final String smellName;
    private final File inputCsvFile;
    private List<String> orderedCommits;
    private final DevelopersHandler developersHandler;
    private SmellChecker smellChecker;

    SmellProcessor(String smellName, File inputCsvFile, List<String> orderedCommits,
                   DevelopersHandler developersHandler, SmellChecker smellChecker) {
        this.smellName = smellName;
        this.inputCsvFile = inputCsvFile;
        this.orderedCommits = orderedCommits;
        this.developersHandler = developersHandler;
        this.smellChecker = smellChecker;
    }

    List<CommitSmell> process() {
        logger.info("Processing smell type: " + smellName);

        Map<String, Collection<InputSmell>> smellMap = mapCommitsToSmells();
        return orderedProcess(smellMap);
    }

    private List<CommitSmell> orderedProcess(Map<String, Collection<InputSmell>> smellMap) {
        List<CommitSmell> commits = new ArrayList<>();
        int previousCommit = -1;
        List<String> previousSmells = new ArrayList<>();
        List<String> currentSmells = new ArrayList<>();
        CommitSmell parsedCommit = null;
        Collection<InputSmell> smells;
        Tuple<Integer, Integer, Integer> smellsCount;
        for (String commit : orderedCommits) {
            logger.trace("Handling commit: " + commit);
            // Smell may be null if no smell has been found on this commit
            if (smellMap.containsKey(commit)) {
                smells = smellMap.get(commit);
                for (InputSmell smell : smells) {

                    if (smell.commitNumber != previousCommit) {
                        if (parsedCommit != null) {
                            // Do not count output smells on first iteration
                            logger.trace("Counting output smells for commit n°" + parsedCommit.commitNumber);
                            smellsCount = compareCommits(parsedCommit.sha, parsedCommit.developer, previousSmells, currentSmells);
                            parsedCommit.setSmells(smellsCount);
                            commits.add(parsedCommit);
                            previousSmells = currentSmells;
                            currentSmells = new ArrayList<>();
                        }

                        logger.trace("Switching to commit n°" + smell.commitNumber);
                        parsedCommit = new CommitSmell(smellName, smell.commitNumber, smell.commitSha, smell.status, smell.developer);
                        currentSmells.add(smell.name);
                        developersHandler.notify(smell.developer);
                        previousCommit = parsedCommit.commitNumber;
                    } else {
                        if (smell.hasSmell()) {
                            currentSmells.add(smell.name);
                        }
                    }
                }
            }
        }

        // Add the last commit if any
        if (parsedCommit != null) {
            smellsCount = compareCommits(parsedCommit.sha, parsedCommit.developer, previousSmells, currentSmells);
            parsedCommit.setSmells(smellsCount);
            commits.add(parsedCommit);
        }
        return commits;
    }

    private Map<String, Collection<InputSmell>> mapCommitsToSmells() {
        Map<String, Collection<InputSmell>> smellMap = new HashMap<>();
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(inputCsvFile));
            if (logger.isTraceEnabled()) {
                logger.trace("Wiping out header line: " + br.readLine());
            } else {
                br.readLine();
            }
            InputSmell smell;
            while ((line = br.readLine()) != null) {
                logger.trace("Parsing line: " + line);
                smell = InputSmell.fromLine(line);
                Collection<InputSmell> smells = smellMap.getOrDefault(smell.commitSha, new HashSet<>());
                smells.add(smell);
                smellMap.put(smell.commitSha, smells);
            }
        } catch (IOException e) {
            logger.error("Unable to read file: " + inputCsvFile);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("Unable to close reader for file: " + inputCsvFile);
                }
            }
        }
        return smellMap;
    }

    private Tuple<Integer, Integer, Integer> compareCommits(String commitHash, String developer,
                                                            List<String> previousInstances, List<String> currentInstances) {
        List<String> intersect = new ArrayList<>(currentInstances);
        intersect.retainAll(previousInstances);

        notifyIntroducedSmells(developer, currentInstances, intersect);

        int countDeleted = notifyRefactoredSmells(commitHash, developer, previousInstances, intersect);

        int countIntroduced = currentInstances.size() - intersect.size();
        int countRefactored = previousInstances.size() - intersect.size() - countDeleted;

        logger.debug("Found " + countIntroduced + " smells introduced, " + countRefactored
                + " smells refactored, and " + countDeleted + " smells deleted.");
        return new Tuple<>(countIntroduced, countRefactored, countDeleted);
    }

    /**
     * Associates the introduced smells to the right developer to handle precise statistics about
     * introduction and refactoring.
     *
     * @param developer        The commit author.
     * @param currentInstances The smells present in the new commit.
     * @param intersect        The intersection between the previous smells and the new ones.
     */
    private void notifyIntroducedSmells(String developer, List<String> currentInstances, List<String> intersect) {
        List<String> introduced = new ArrayList<>(currentInstances);
        introduced.removeAll(intersect);
        for (String smell : introduced) {
            developersHandler.notifyIntroduced(developer, smell);
        }
    }

    /**
     * Associates the refactored smells to the right developer to handle precise statistics about
     * introduction and refactoring.
     *
     * @param commitHash        The commit in which the refactoring is happening.
     * @param developer         The commit author.
     * @param previousInstances The previously existing smells.
     * @param intersect         The intersection between the previous smells and the new ones.
     * @return The number of deleted smells, i.e. the smells that are not present anymore but were not actually refactored.
     */
    private int notifyRefactoredSmells(String commitHash, String developer,
                                       List<String> previousInstances, List<String> intersect) {
        int nbDeleted = 0;
        List<String> refactored = new ArrayList<>(previousInstances);
        refactored.removeAll(intersect);
        for (String smell : refactored) {
            if (smellChecker.isActualRefactoring(SmellCode.valueOf(smellName), smell, commitHash)) {
                developersHandler.notifyRefactored(developer, smell);
            } else {
                developersHandler.notifyDeleted(developer, smell);
                nbDeleted++;
            }
        }
        return nbDeleted;
    }
}
