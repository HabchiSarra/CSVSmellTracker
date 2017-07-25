package org.tandoori.metrics.calculator.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tandoori.metrics.calculator.DevelopersHandler;
import org.tandoori.metrics.calculator.processing.CommitSmell;
import org.tandoori.metrics.calculator.processing.Tuple;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static org.tandoori.metrics.calculator.processing.SmellsProcessor.NO_SMELL_CODE;

/**
 * Count the overall smells introduction and refactoring per developer
 */
public class PerDevSummaryWriter extends CommonSmellSummaryWriter implements SmellWriter {
    private static final Logger logger = LoggerFactory.getLogger(PerDevSummaryWriter.class.getName());
    private static final String DEV_ID = "dev_id";

    /**
     * {@link SmellStore} sorted by developer.
     */
    private SmellStore devIntroducedRefactored;


    public PerDevSummaryWriter(File outputFile, DevelopersHandler devHandler) {
        super(outputFile, devHandler);
        devIntroducedRefactored = new SmellStore();
    }

    @Override
    protected void writeValues(List<CommitSmell> commits, FileWriter fileWriter) {
        int firstCommitNb = 0;
        if (!commits.isEmpty()) {
            firstCommitNb = commits.get(0).commitNumber;
        } else {
            logger.warn("No commits provided, output file will be empty");
        }        for (CommitSmell commit : commits) {
            // We won't count the smells introduced in the first commit
            // since it will contain every smells in the code so far.
            if (commit.commitNumber == firstCommitNb) {
                continue;
            }
            if (!commit.smellName.equals(NO_SMELL_CODE)) {
                devIntroducedRefactored.addSmells(commit.developer, commit.introduced(), commit.refactored());
            }
        }
        writeCommitLine(fileWriter);
    }

    @Override
    protected List<String> getHeaderLine() {
        List<String> header = new ArrayList<>();
        header.add(DEV_ID);
        header.add(INTRODUCED_KEY);
        header.add(REFACTORED_KEY);
        return header;
    }

    private void writeCommitLine(FileWriter fileWriter) {
        String[] devs = devHandler.sortedDevelopers();
        List<String> line;

        Tuple<Integer, Integer> smells;
        for (String dev : devs) {
            smells = devIntroducedRefactored.get(dev);

            line = new ArrayList<>();
            line.add(dev);
            line.add(String.valueOf(smells.introduced));
            line.add(String.valueOf(smells.refactored));

            printLine(fileWriter, line);
        }
    }
}
