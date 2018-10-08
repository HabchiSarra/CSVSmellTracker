package fr.inria.sniffer.metrics.calculator.writer;

import fr.inria.sniffer.metrics.calculator.DevelopersHandler;
import fr.inria.sniffer.metrics.calculator.SmellCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.inria.sniffer.metrics.calculator.processing.CommitSmell;
import fr.inria.sniffer.metrics.calculator.processing.Tuple;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.inria.sniffer.metrics.calculator.processing.SmellsProcessor.NO_SMELL_CODE;

/**
 * Count the smells introduced and refactored per smell type per developer
 */
public class PerDevPerSmellSummaryWriter extends CommonSmellSummaryWriter implements SmellWriter {
    private static final Logger logger = LoggerFactory.getLogger(PerDevPerSmellSummaryWriter.class.getName());

    private static final String SMELL = "smell";

    /**
     * Contains a {@link SmellStore} sorted by developer for each smell.
     */
    private Map<String, SmellStore> smellIntroducedRefactored;

    public PerDevPerSmellSummaryWriter(File outputFile, DevelopersHandler devHandler) {
        super(outputFile, devHandler);
        smellIntroducedRefactored = new HashMap<>();

        for (SmellCode smellCode : SmellCode.values()) {
            smellIntroducedRefactored.put(smellCode.name(), new SmellStore());
        }
    }

    @Override
    protected void writeValues(List<CommitSmell> commits, FileWriter fileWriter) {
        int firstCommitNb = 0;
        if (!commits.isEmpty()) {
            firstCommitNb = commits.get(0).commitNumber;
        } else {
            logger.warn("No commits provided, output file will be empty");
        }
        for (CommitSmell commit : commits) {
            // We won't count the smells introduced in the first commit
            // since it will contain every smells in the code so far.
            if (commit.commitNumber == firstCommitNb) {
                continue;
            }
            if (!commit.smellName.equals(NO_SMELL_CODE)) {
                SmellStore store = smellIntroducedRefactored.get(commit.smellName);
                store.addSmells(commit.developer, commit.introduced(), commit.refactored(), commit.deleted());
            }
        }
        writeCommitLine(fileWriter);
    }

    @Override
    protected List<String> getHeaderLine() {
        List<String> header = new ArrayList<>();
        header.add(SMELL);
        for (String devId : devHandler.sortedDevelopers()) {
            header.add(devId + "-" + INTRODUCED_KEY);
            header.add(devId + "-" + REFACTORED_KEY);
            header.add(devId + "-" + DELETED_KEY);
        }
        return header;
    }

    private void writeCommitLine(FileWriter fileWriter) {
        List<String> line;

        Tuple<Integer, Integer, Integer> smells;
        SmellStore store;
        for (SmellCode smell : SmellCode.values()) {
            line = new ArrayList<>();
            line.add(smell.name());

            store = smellIntroducedRefactored.get(smell.name());
            for (String devId : devHandler.sortedDevelopers()) {
                smells = store.get(devId);
                line.add(String.valueOf(smells.introduced));
                line.add(String.valueOf(smells.refactored));
                line.add(String.valueOf(smells.deleted));
            }
            printLine(fileWriter, line);
        }
    }
}
