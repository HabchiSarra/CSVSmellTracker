package fr.inria.sniffer.metrics.calculator.processing;


import fr.inria.sniffer.metrics.calculator.DevelopersHandler;
import fr.inria.sniffer.metrics.calculator.SmellCode;
import fr.inria.sniffer.metrics.calculator.processing.verification.SmellChecker;
import fr.inria.sniffer.metrics.calculator.writer.SmellWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmellsProcessor {
    private static final Logger logger = LoggerFactory.getLogger(SmellsProcessor.class.getName());
    /**
     * Supplementary file describing commits without any smell.
     *
     * This file enables to make the refactored smells appear
     * when every smells disappeared from the application code.
     */
    public static final String NO_SMELL_CODE = "NOSMELL";

    private List<File> smellFiles;
    private final DevelopersHandler devHandler;
    private List<String> orderedCommits;
    private SmellChecker smellChecker;
    private final Set<SmellWriter> outputs;

    public SmellsProcessor(List<File> smellFiles, DevelopersHandler devHandler,
                           List<String> orderedCommits, SmellChecker smellChecker) {
        this.smellFiles = smellFiles;
        this.devHandler = devHandler;
        this.orderedCommits = orderedCommits;
        this.smellChecker = smellChecker;
        outputs = new HashSet<>();
    }

    public void addOutput(SmellWriter output) {
        outputs.add(output);
    }

    public void process() {
        SmellProcessor processor;
        String smell;
        List<CommitSmell> commits = new ArrayList<>();
        for (File smellFile : smellFiles) {
            smell = getSmellName(smellFile);
            // If we can't parse the file name we consider it as non-smell file
            if (smell != null) {
                logger.info("Processing smell file: " + smellFile.getName());
                processor = new SmellProcessor(smell, smellFile, orderedCommits, devHandler, smellChecker);
                commits.addAll(processor.process());
            }
        }

        for (SmellWriter output : outputs) {
            output.write(commits);
        }
    }

    /**
     * We consider the raw filename from Paprika e.g. 2017_7_18_11_25_HMU.csv
     *
     * @param smellFile
     * @return
     */
    private String getSmellName(File smellFile) {
        String[] split = smellFile.getName().split("_");
        String smellName;

        // Parsing the file name
        try {
            smellName = split[split.length - 1].split("\\.")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.warn("Unable to parse smell name for file: " + smellFile.getName());
            return null;
        }

        // Parsing the smell name
        try {
            // We have a file containing analyzed commits without any smell
            if (smellName.equals(NO_SMELL_CODE)) {
                return NO_SMELL_CODE;
            }
            return SmellCode.valueOf(smellName).name();
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown smell name: " + smellName);
            return null;
        }
    }
}
