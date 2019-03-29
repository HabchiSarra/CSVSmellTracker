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
package fr.inria.sniffer.metrics.calculator.writer;

import fr.inria.sniffer.metrics.calculator.SmellCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.inria.sniffer.metrics.calculator.DevelopersHandler;
import fr.inria.sniffer.metrics.calculator.processing.CommitSmell;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static fr.inria.sniffer.metrics.calculator.processing.SmellsProcessor.NO_SMELL_CODE;

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
            if (!commit.smellName.equals(NO_SMELL_CODE)) {
                currentOutput.setSmellCount(commit.smellName, commit.introduced(), commit.refactored(), commit.deleted());
            }
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
                header.add(devId + "-" + smellCode.name() + "-" + DELETED_KEY);
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
