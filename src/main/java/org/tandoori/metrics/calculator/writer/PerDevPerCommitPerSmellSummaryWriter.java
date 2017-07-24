package org.tandoori.metrics.calculator.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tandoori.metrics.calculator.DevelopersHandler;
import org.tandoori.metrics.calculator.SmellCode;
import org.tandoori.metrics.calculator.processing.CommitSmell;
import org.tandoori.metrics.calculator.utils.CSVUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Count the smells introduced and refactored per per developer and per commit
 */
public class PerDevPerCommitPerSmellSummaryWriter extends CommonSmellSummaryWriter implements SmellWriter {
    private static final Logger logger = LoggerFactory.getLogger(PerDevPerCommitPerSmellSummaryWriter.class.getName());

    private static final String COMMIT_NUMBER = "commitNumber";
    private static final String SHA = "sha";
    private static final String STATUS = "status";

    public PerDevPerCommitPerSmellSummaryWriter(File outputFile, DevelopersHandler devHandler) {
        super(outputFile, devHandler);
    }

    @Override
    protected void writeValues(List<CommitSmell> commits, FileWriter fileWriter) {
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

    @Override
    protected List<String> getHeaderLine() {
        List<String> header = new ArrayList<>();
        header.add(COMMIT_NUMBER);
        header.add(SHA);
        header.add(STATUS);
        for (String devId : devHandler.sortedDevelopers()) {
            for (SmellCode smellCode : SmellCode.values()) {
                header.add(devId + "-" + smellCode.name() + "-" + INTRODUCED_KEY);
                header.add(devId + "-" + smellCode.name() + "-" + REFACTORED_KEY);
            }
        }
        return header;
    }

    private void writeCommitLine(FileWriter fileWriter, CommitOutput commit) {
        int devOffset = devHandler.getOffset(commit.developer);
        int totalDev = devHandler.size();
        logger.trace("Printing results for commit: " + commit.sha);
        printLine(fileWriter, commit.prepareOutputLine(devOffset, totalDev));
    }
}
