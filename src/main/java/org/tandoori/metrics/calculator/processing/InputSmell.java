package org.tandoori.metrics.calculator.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse a Tandoori smell analysis CSV line.
 *
 * We are expecting data of the form:
 * commitNumber, commitSha, smellInstance, developer, commitStatus
 */
class InputSmell {
    private static final Logger logger = LoggerFactory.getLogger(InputSmell.class.getName());
    private static final String SEPARATOR = ",";

    final String name;
    final int commitNumber;
    final String commitSha;
    final String developer;
    final String status;

    private InputSmell(String name, int commitNumber, String commitSha, String developer, String status) {
        this.name = name;
        this.commitNumber = commitNumber;
        this.commitSha = commitSha;
        this.developer = developer;
        this.status = status;
    }

    public static InputSmell fromLine(String line){
        String[] content = line.split(SEPARATOR);
        if (content.length < 4) {
            logger.warn("Unable to parse smell input: " + line);
        }
        return new InputSmell(content[2], Integer.valueOf(content[0]), content[1], content[3], content[4]);
    }
}
