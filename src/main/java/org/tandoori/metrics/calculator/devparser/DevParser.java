package org.tandoori.metrics.calculator.devparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DevParser {
    private static final Logger logger = LoggerFactory.getLogger(DevParser.class.getName());

    private final File inputCsvFile;
    private Set<String> parsedHash;

    private Project project;
    private Map<String, Developer> developers;

    public DevParser(File inputCsvFile, Set<String> parsedHash) {
        this.inputCsvFile = inputCsvFile;
        this.parsedHash = parsedHash;
    }

    public void parse() {
        initParser();

        BufferedReader br = null;
        String line;
        logger.info("Processing project file type: " + inputCsvFile.getAbsolutePath());
        try {
            br = new BufferedReader(new FileReader(inputCsvFile));
            if (logger.isTraceEnabled()) {
                logger.trace("Wiping out header line: " + br.readLine());
            } else {
                br.readLine();
            }
            while ((line = br.readLine()) != null) {
                logger.trace("Parsing line: " + line);
               process(line);
            }

        } catch (IOException e) {
            logger.error("Unable to read file: " + inputCsvFile);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("Unable to close reader for file: " + inputCsvFile);
                }
            }
        }
    }

    private void initParser() {
        project = new Project();
        developers = new HashMap<>();
    }

    private void process(String line) {
        InputCommit commit = InputCommit.fromInputLine(line);
        boolean isAnalyzed = parsedHash.contains(commit.hash);

        project.addCommit(isAnalyzed);

        Developer developer = developers.getOrDefault(commit.userId,
                new Developer(commit.userId, commit.userName, commit.userEmail));
        developer.addCommit(isAnalyzed);
        developers.put(developer.id, developer);
    }

    public Project getProject() {
        return project;
    }

    public Map<String, Developer> getDevelopers() {
        return developers;
    }
}
