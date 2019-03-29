/**
 *   Sniffer - Analyze the history of Android code smells at scale.
 *   Copyright (C) 2019 Sarra Habchi
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.inria.sniffer.metrics.calculator;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.inria.sniffer.metrics.calculator.processing.SmellsProcessor;
import fr.inria.sniffer.metrics.calculator.processing.verification.HappySmellChecker;
import fr.inria.sniffer.metrics.calculator.processing.verification.neo4j.Neo4jSmellChecker;
import fr.inria.sniffer.metrics.calculator.processing.verification.SmellChecker;
import fr.inria.sniffer.metrics.calculator.writer.PerDevPerCommitPerSmellSummaryWriter;
import fr.inria.sniffer.metrics.calculator.writer.PerDevPerSmellSummaryWriter;
import fr.inria.sniffer.metrics.calculator.writer.PerDevSummaryWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
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

    @Option(name = "-c", required = true, usage = "Set the project commits file with developers")
    public File commitFile;

    @Option(name = "-p", usage = "Set the project name")
    public String projectName;

    @Option(name = "-l", required = true, usage = "Project commits in topological order")
    public File logs;


    @Option(name = "-db", usage = "Provide project database path for further smell refactoring verification")
    public File dbPath;

    public void generateReport() {
        List<File> smellsFile = getFiles();
        DevelopersHandler devHandler = new DevelopersHandlerImpl();
        SmellChecker smellChecker = dbPath != null ? new Neo4jSmellChecker(dbPath) : new HappySmellChecker();
        SmellsProcessor smellsProcessor = new SmellsProcessor(smellsFile, devHandler, getCommitInOrder(), smellChecker);
        if (outputDir.isFile()) {
            logger.error("Output should be a directory, got a file instead: " + outputDir.getAbsolutePath());
            return;
        }
        boolean mkdirs = outputDir.mkdirs();
        if (!mkdirs) {
            logger.warn("Unable to create directory: " + outputDir.getAbsolutePath());
        }
        File outputFile = new File(outputDir, "metrics-perDev-perCommit-perSmell.csv");
        smellsProcessor.addOutput(new PerDevPerCommitPerSmellSummaryWriter(outputFile, devHandler));

        outputFile = new File(outputDir, "metrics-perDev-perSmell.csv");
        smellsProcessor.addOutput(new PerDevPerSmellSummaryWriter(outputFile, devHandler));

        outputFile = new File(outputDir, "metrics-perDev.csv");
        smellsProcessor.addOutput(new PerDevSummaryWriter(outputFile, devHandler, commitFile, projectName));

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

    private List<String> getCommitInOrder() {
        BufferedReader br = null;
        String line;
        List<String> commits = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(logs));
            while ((line = br.readLine()) != null) {
                commits.add(line);
            }
        } catch (IOException e) {
            logger.error("Unable to read file: " + logs.getAbsolutePath());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("Unable to close reader for file: " + logs.getAbsolutePath());
                }
            }
        }
        return commits;
    }
}
