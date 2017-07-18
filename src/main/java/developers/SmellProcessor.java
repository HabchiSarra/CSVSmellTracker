package developers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarra on 18/07/17.
 */
public class SmellProcessor {
    private static final String SEPARATOR = ",";
    private final File inputCsvFile;
    private DeveloperSet developerSet;

    public SmellProcessor(File inputCsvFile, DeveloperSet developerSet) {
        this.inputCsvFile = inputCsvFile;
        this.developerSet = developerSet;
    }

    public List<Commit> process() {
        BufferedReader br = null;
        String line;
        List<Commit> commits = new ArrayList<Commit>();
        int currentCommit;
        int previousCommit = 0;
        List<String> previousSmells = new ArrayList<String>();
        List<String> currentSmells = new ArrayList<String>();
        int lineNumber = 0;
        try {

            br = new BufferedReader(new FileReader(inputCsvFile));
            Commit parsedCommit;
            String developer;
            while ((line = br.readLine()) != null) {
                String[] lineContent = line.split(SEPARATOR);
                if (lineContent.length < 4) {
                    System.err.println("Error : line number " + lineNumber);
                }
                currentCommit = Integer.valueOf(lineContent[0]);
                if (currentCommit == previousCommit) {
                    currentSmells.add(lineContent[2]);
                } else {
                    developer = lineContent[3];
                    developerSet.notify(developer);
                    parsedCommit = new Commit(currentCommit, lineContent[1], developer);
                    parsedCommit.setSmells(compareCommits(previousSmells, currentSmells));
                    commits.add(parsedCommit);

                    previousSmells = currentSmells;
                    currentSmells = new ArrayList<String>();
                }
                lineNumber++;
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
