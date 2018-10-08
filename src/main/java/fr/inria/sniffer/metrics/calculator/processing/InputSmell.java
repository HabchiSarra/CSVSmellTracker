package fr.inria.sniffer.metrics.calculator.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse a SmellDetector smell analysis CSV line.
 *
 * We are expecting data of the form:
 * commitNumber, commitSha, smellInstance, commitStatus, developer
 */
class InputSmell {
    private static final Logger logger = LoggerFactory.getLogger(InputSmell.class.getName());
    private static final String SEPARATOR = ",";
    private static final String NO_SMELL_KW = "-";

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

    /**
     * Tells if the current Input actually defines a smell
     * or declares an absence of any smell.
     *
     * @return true if the commit has an actual smell, false otherwise.
     */
    public boolean hasSmell() {
        return !name.equals(NO_SMELL_KW);
    }

    public static InputSmell fromLine(String line) {
        String[] content = line.split(SEPARATOR);
        if (content.length < 5) {
            logger.warn("Unable to parse smell input: " + line);
        }
        return new InputSmell(content[2], Integer.valueOf(content[0]), content[1], content[4], content[3]);
    }
}
