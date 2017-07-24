package org.tandoori.metrics.calculator.processing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tandoori.metrics.calculator.DevelopersHandler;
import org.tandoori.metrics.calculator.SmellCode;
import org.tandoori.metrics.calculator.writer.SmellWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmellsProcessor implements DevelopersHandler {
    private static final Logger logger = LoggerFactory.getLogger(SmellsProcessor.class.getName());
    private final Map<String, Integer> developersCode = new HashMap<String, Integer>();

    private List<File> smellFiles;
    private final Set<SmellWriter> outputs;

    public SmellsProcessor(List<File> smellFiles) {
        this.smellFiles = smellFiles;
        outputs = new HashSet<>();
    }

    public void addOutput(SmellWriter output) {
        outputs.add(output);
    }

    public void process() {
        SmellProcessor processor;
        SmellCode smell;
        List<CommitSmell> commits = new ArrayList<>();
        for (File smellFile : smellFiles) {
            smell = getSmellName(smellFile);
            // If we can't parse the file name we consider it as non-smell file
            if (smell != null) {
                logger.info("Processing smell file: " + smellFile.getName());
                processor = new SmellProcessor(smell.name(), smellFile, this);
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
    private SmellCode getSmellName(File smellFile) {
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
            return SmellCode.valueOf(smellName);
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown smell name: " + smellName);
            return null;
        }
    }

    public void notify(String developer) {
        if (!developersCode.containsKey(developer)) {
            logger.trace("New developer notified: " + developer);
            developersCode.put(developer, developersCode.size() + 1);
        }
    }

    @Override
    public int size() {
        return developersCode.size();
    }

    @Override
    public int getOffset(String devName) {
        return developersCode.get(devName);
    }

    @Override
    public String[] sortedDevelopers() {
        String[] sorted = new String[developersCode.size()];
        for (String devId : developersCode.keySet()) {
            sorted[developersCode.get(devId) - 1] = devId;
        }
        return sorted;
    }
}
