package org.tandoori.metrics.calculator.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tandoori.metrics.calculator.DevelopersHandler;

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

    SmellProcessor(String smellName, File inputCsvFile, List<String> orderedCommits, DevelopersHandler developersHandler) {
        this.smellName = smellName;
        this.inputCsvFile = inputCsvFile;
        this.orderedCommits = orderedCommits;
        this.developersHandler = developersHandler;
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
        for (String commit : orderedCommits) {
            logger.debug("Handling commit: " + commit);
            // Smell may be null if no smell has been found on this commit
            if (smellMap.containsKey(commit)) {
                smells = smellMap.get(commit);
                for (InputSmell smell : smells) {

                    if (smell.commitNumber != previousCommit) {
                        if (parsedCommit != null) {
                            // Do not count output smells on first iteration
                            logger.trace("Counting output smells for commit n°" + parsedCommit.commitNumber);
                            parsedCommit.setSmells(compareCommits(parsedCommit.developer, previousSmells, currentSmells));
                            commits.add(parsedCommit);
                            previousSmells = currentSmells;
                            currentSmells = new ArrayList<>();
                        }

                        logger.debug("Switching to commit n°" + smell.commitNumber);
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
            parsedCommit.setSmells(compareCommits(parsedCommit.developer, previousSmells, currentSmells));
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

    private Tuple<Integer, Integer> compareCommits(String developer, List<String> previousInstances, List<String> currentInstances) {
        List<String> intersect = new ArrayList<>(currentInstances);
        intersect.retainAll(previousInstances);

        notifyIntroducedSmells(developer, currentInstances, intersect);

        notifyRefactoredSmells(developer, previousInstances, intersect);

        int countIntroduced = currentInstances.size() - intersect.size();
        int countRefactored = previousInstances.size() - intersect.size();
        logger.debug("Found " + countIntroduced + " smells introduced and " + countRefactored + " smells refactored");
        return new Tuple<>(countIntroduced, countRefactored);
    }

    private void notifyIntroducedSmells(String developer, List<String> currentInstances, List<String> intersect) {
        List<String> introduced = new ArrayList<>(currentInstances);
        introduced.removeAll(intersect);
        for (String smell : introduced) {
            developersHandler.notifyIntroduced(developer, smell);
        }
    }

    private void notifyRefactoredSmells(String developer, List<String> previousInstances, List<String> intersect) {
        List<String> refactored = new ArrayList<>(previousInstances);
        refactored.removeAll(intersect);
        for (String smell : refactored) {
            developersHandler.notifyRefactored(developer, smell);
        }
    }
}
