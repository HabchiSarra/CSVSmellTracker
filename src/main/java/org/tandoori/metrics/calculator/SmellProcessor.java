package org.tandoori.metrics.calculator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarra on 18/07/17.
 */
public class SmellProcessor {
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
        List<CommitSmell> commits = new ArrayList<CommitSmell>();
        int previousCommit = -1;
        List<String> previousSmells = new ArrayList<String>();
        List<String> currentSmells = new ArrayList<String>();
        try {

            br = new BufferedReader(new FileReader(inputCsvFile));
            CommitSmell parsedCommit;
            InputSmell smell;
            while ((line = br.readLine()) != null) {
                smell = InputSmell.fromLine(line);
                if (smell.commitNumber == previousCommit) {
                    currentSmells.add(smell.name);
                } else {
                    developersHandler.notify(smell.developer);
                    parsedCommit = new CommitSmell(smellName,
                            smell.commitNumber, smell.commitSha,
                            smell.status, smell.developer);
                    parsedCommit.setSmells(compareCommits(previousSmells, currentSmells));
                    commits.add(parsedCommit);

                    previousSmells = currentSmells;
                    currentSmells = new ArrayList<String>();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return commits;
    }

    private Tuple<Integer, Integer> compareCommits(List<String> previousInstances, List<String> currentInstances) {
        List<String> intersect = new ArrayList<String>(currentInstances);
        intersect.retainAll(previousInstances);

        int countIntroduced = currentInstances.size() - intersect.size();
        int countRefactored = previousInstances.size() - intersect.size();
        return new Tuple<Integer, Integer>(countIntroduced, countRefactored);
    }
}
