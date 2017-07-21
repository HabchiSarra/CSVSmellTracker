package org.tandoori.metrics.calculator.writer;

import org.tandoori.metrics.calculator.processing.CommitSmell;
import org.tandoori.metrics.calculator.DevelopersHandler;
import org.tandoori.metrics.calculator.SmellCode;
import org.tandoori.metrics.calculator.utils.CSVUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sarra on 20/07/17.
 */
public class SmellSummaryWriter {
    public static final String REFACTORED_KEY = "R";
    public static final String INTRODUCED_KEY = "I";
    public static final String COMMIT_NUMBER = "commitNumber";
    public static final String SHA = "sha";
    public static final String STATUS = "status";
    private final File outputCsvFile;
    private DevelopersHandler devHandler;

    public SmellSummaryWriter(File outputFile, DevelopersHandler devHandler) {
        this.outputCsvFile = outputFile;
        this.devHandler = devHandler;
    }

    /**
     * Handles the {@link FileWriter} openning and record the values.
     *
     * @param smells The analyzed smells to write out.
     */
    public void write(List<CommitSmell> smells) {
        FileWriter fileWriter = null;
        try {
            System.out.println("Writing output file:" + outputCsvFile.getName());
            fileWriter = new FileWriter(outputCsvFile);
            recordValues(smells, fileWriter);
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
        writeHeaderLine(fileWriter);
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

    private void writeHeaderLine(Writer fileWriter) {
        try {
            CSVUtils.writeLine(fileWriter, getHeaderLine());
        } catch (IOException e) {
            System.err.println("Unable to print header line: " + e.getLocalizedMessage());
        }
    }

    private List<String> getHeaderLine() {
        List<String> header = new ArrayList<>();
        header.add(COMMIT_NUMBER);
        header.add(SHA);
        header.add(STATUS);
        for (String devId : devHandler.sortedDevelopers()) {
            for (SmellCode smellCode : SmellCode.values()) {
                header.add(devId + "-" + smellCode.name() + "-" + REFACTORED_KEY);
                header.add(devId + "-" + smellCode.name() + "-" + INTRODUCED_KEY);
            }
        }
        return header;
    }

    private void writeCommitLine(FileWriter fileWriter, CommitOutput commit) {
        try {
            int devOffset = devHandler.getOffset(commit.developer);
            int totalDev = devHandler.size();
            CSVUtils.writeLine(fileWriter, commit.prepareOutputLine(devOffset, totalDev));
        } catch (IOException e) {
            System.err.println("Unable to print line for commit" + commit.sha + ": " + e.getLocalizedMessage());
        }
    }
}
