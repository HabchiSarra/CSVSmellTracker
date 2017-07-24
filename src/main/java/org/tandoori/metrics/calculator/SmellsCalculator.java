package org.tandoori.metrics.calculator;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tandoori.metrics.calculator.processing.SmellsProcessor;
import org.tandoori.metrics.calculator.writer.PerDevPerCommitPerSmellSummaryWriter;
import org.tandoori.metrics.calculator.writer.PerDevPerSmellSummaryWriter;
import org.tandoori.metrics.calculator.writer.PerDevSummaryWriter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sarra on 20/07/17.
 */
public class SmellsCalculator {
    private static final Logger logger = LoggerFactory.getLogger(SmellsCalculator.class.getName());

    @Option(name = "-i", handler = StringArrayOptionHandler.class, forbids = "-d",
            usage = "List the files to open")
    public List<String> inputFilesPaths;

    @Option(name = "-d", forbids = "-a",
            usage = "Give a directory containing reports files as .csv")
    public File inputDir;

    @Option(name = "-o", required = true, usage = "Set the output dir")
    public File outputDir;

    public void generateReport() {
        List<File> smellsFile = getFiles();
        SmellsProcessor smellsProcessor = new SmellsProcessor(smellsFile);
        if (outputDir.isFile()) {
            logger.error("Output should be a directory, got a file instead: " + outputDir.getAbsolutePath());
            return;
        }
        boolean mkdirs = outputDir.mkdirs();
        if (!mkdirs) {
            logger.warn("Unable to create directory: " + outputDir.getAbsolutePath());
        }
        File outputFile = new File(outputDir, "metrics-perDev-perCommit-perSmell.csv");
        smellsProcessor.addOutput(new PerDevPerCommitPerSmellSummaryWriter(outputFile, smellsProcessor));

        outputFile = new File(outputDir, "metrics-perDev-perSmell.csv");
        smellsProcessor.addOutput(new PerDevPerSmellSummaryWriter(outputFile, smellsProcessor));

        outputFile = new File(outputDir, "metrics-perDev.csv");
        smellsProcessor.addOutput(new PerDevSummaryWriter(outputFile, smellsProcessor));

        smellsProcessor.process();
    }

    /**
     * Correctly parse the input in case of a directory to parse or a list of filename.
     *
     * @return The list
     */
    private List<File> getFiles() {
        if (inputDir != null) {
            File[] files = inputDir.listFiles(new SmellFileFilter());
            return files != null ? Arrays.asList(files) : Collections.<File>emptyList();
        }
        List<File> inputFiles = new ArrayList<>();
        if (inputFilesPaths != null) {
            for (String inputFilesPath : inputFilesPaths) {
                inputFiles.add(new File(inputFilesPath));
            }
        }
        return inputFiles;
    }

    private class SmellFileFilter implements FilenameFilter {
        public boolean accept(File file, String s) {
            return s.toLowerCase().endsWith(".csv");
        }
    }
}
