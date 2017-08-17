package org.tandoori.metrics.calculator.devparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse a commit input from the output of GitMiner.
 *
 * We are expecting an input of the form:
 * hash, login, id, mail
 */
public class InputCommit {
    private static final Logger logger = LoggerFactory.getLogger(InputCommit.class.getName());

    private static final String SEPARATOR = ",";

    public final String hash;
    public final String userName;
    public final String userEmail;
    public final String userId;

    private InputCommit(String hash, String userName, String userEmail, String userId) {
        this.hash = hash;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
    }

    public static InputCommit fromInputLine(String line) {
        String[] content = line.split(SEPARATOR);
        if (content.length < 4) {
            logger.warn("Unable to parse commit input: " + line);
        }
        return new InputCommit(content[0], content[1], content[3], content[2]);
    }
}
