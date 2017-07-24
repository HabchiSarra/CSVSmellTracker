package org.tandoori.metrics.calculator.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tandoori.metrics.calculator.DevelopersHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarra on 18/07/17.
 */
class SmellProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SmellProcessor.class.getName());

    private final String smellName;
    private final File inputCsvFile;
    private final DevelopersHandler developersHandler;

    public SmellProcessor(String smellName, File inputCsvFile, DevelopersHandler developersHandler) {
        this.smellName = smellName;
        this.inputCsvFile = inputCsvFile;
        this.developersHandler = developersHandler;
    }

    public List<CommitSmell> process() {
        BufferedReader br = null;
        String line;
        List<CommitSmell> commits = new ArrayList<>();
        int previousCommit = -1;
        List<String> previousSmells = new ArrayList<>();
        List<String> currentSmells = new ArrayList<>();

        logger.info("Processing smell type: " + smellName);
        try {
            br = new BufferedReader(new FileReader(inputCsvFile));
            CommitSmell parsedCommit = null;
            InputSmell smell;
            if (logger.isTraceEnabled()) {
                logger.trace("Wiping out header line: " + br.readLine());
            } else {
                br.readLine();
            }
            while ((line = br.readLine()) != null) {
                logger.trace("Parsing line: " + line);
                smell = InputSmell.fromLine(line);

                if (smell.commitNumber != previousCommit) {
                    if (parsedCommit != null) {
                        // Do not count output smells on first iteration
                        logger.trace("Counting output smells for commit n°" + parsedCommit.commitNumber);
                        parsedCommit.setSmells(compareCommits(previousSmells, currentSmells));
                        commits.add(parsedCommit);
                        previousSmells = currentSmells;
                        currentSmells = new ArrayList<>();
                    }

                    logger.debug("Switching to commit n°" + smell.commitNumber);
                    developersHandler.notify(smell.developer);
                    parsedCommit = new CommitSmell(smellName, smell.commitNumber, smell.commitSha, smell.status, smell.developer);

                    currentSmells.add(smell.name);


                    previousCommit = parsedCommit.commitNumber;

                } else {
                    currentSmells.add(smell.name);
                }
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
        return commits;
    }

    private Tuple<Integer, Integer> compareCommits(List<String> previousInstances, List<String> currentInstances) {
        List<String> intersect = new ArrayList<>(currentInstances);
        intersect.retainAll(previousInstances);

        int countIntroduced = currentInstances.size() - intersect.size();
        int countRefactored = previousInstances.size() - intersect.size();
        logger.debug("Found " + countIntroduced + " smells introduced and " + countRefactored + " smells refactored");
        return new Tuple<>(countIntroduced, countRefactored);
    }
}
