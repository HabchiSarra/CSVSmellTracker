package developers;


import utils.CSVUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SmellsProcessor implements DeveloperSet {
    private final Map<String, Integer> developersCode = new HashMap<String, Integer>();

    private List<File> smellFiles;
    private final File outputCsvFile;

    public SmellsProcessor(List<File> smellFiles, File outputCsvFile) {
        this.smellFiles = smellFiles;
        this.outputCsvFile = outputCsvFile;
    }

    public void process() {
        SmellProcessor processor;
        String smellName;
        List<CommitSmell> commits = new ArrayList<CommitSmell>();
        for (File smellFile : smellFiles) {
            smellName = getSmellName(smellFile);
            processor = new SmellProcessor(smellName, smellFile, this);
            commits.addAll(processor.process());
        }
        record(commits);
    }

    /**
     * We consider the raw filename from Paprika e.g. 2017_7_18_11_25_HMU.csv
     *
     * @param smellFile
     * @return
     */
    private String getSmellName(File smellFile) {
        String[] split = smellFile.getName().split("_");
        return split[split.length - 1].split(".")[0];
    }

    /**
     * Handles the {@link FileWriter} openning and record the values.
     *
     * @param commits The analyzed smells to write out.
     */
    public void record(List<CommitSmell> commits) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(outputCsvFile);
            recordValues(commits, fileWriter);
        } catch (IOException e) {
            System.err.println("Unable to open file " + outputCsvFile.getAbsolutePath() + ": " + e.getLocalizedMessage());
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    System.err.println("Unable to close file " + outputCsvFile.getAbsolutePath() + ": " + e.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * Actual transformation and record of the output.
     *
     * @param commits
     * @param fileWriter
     */
    private void recordValues(List<CommitSmell> commits, FileWriter fileWriter) {
        Collections.sort(commits);
        CommitOutput currentOutput = null;
        for (CommitSmell commit : commits) {
            if (currentOutput == null) {
                // First iteration, we have to create a new commit
                currentOutput = new CommitOutput(commit.commitNumber, commit.sha, commit.developer, commit.status);
            } else if (commit.commitNumber != currentOutput.commitNumber) {
                // Writing current commit summary and switching to next
                writeCommitLine(fileWriter, currentOutput);
                currentOutput = new CommitOutput(commit.commitNumber, commit.sha, commit.developer, commit.status);
            }
            // Add the stats of the current smell to the right commit.
            currentOutput.setSmellCount(commit.smellName, commit.introduced(), commit.refactored());
        }
    }

    private void writeCommitLine(FileWriter fileWriter, CommitOutput commit) {
        try {
            int devOffset = developersCode.get(commit.developer);
            int totalDev = developersCode.size();
            CSVUtils.writeLine(fileWriter, commit.prepareOutputLine(devOffset, totalDev));
        } catch (IOException e) {
            System.err.println("Unable to print line for commit" + commit.sha + ": " + e.getLocalizedMessage());
        }
    }

    public void notify(String developer) {
        if (!developersCode.containsKey(developer)) {
            developersCode.put(developer, developersCode.size() + 1);
        }
    }
}
