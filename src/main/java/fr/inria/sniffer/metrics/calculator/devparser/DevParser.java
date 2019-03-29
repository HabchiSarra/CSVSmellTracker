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
package fr.inria.sniffer.metrics.calculator.devparser;

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
