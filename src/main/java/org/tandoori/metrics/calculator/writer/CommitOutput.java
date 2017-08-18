package org.tandoori.metrics.calculator.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tandoori.metrics.calculator.SmellCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sarra on 20/07/17.
 */
class CommitOutput {
    private static final Logger logger = LoggerFactory.getLogger(CommitOutput.class.getName());
    /**
     * Right now we have the columns: introduced, refactored, deleted
     */
    public static final int NB_COLUMN_PER_SMELL = 3;

    public final int commitNumber;
    public final String sha;
    public final String developer;
    public final String status;

    private static final String EMPTY_CELL = "0";
    private static final int NB_SMELLS = SmellCode.values().length;

    private final int[] introducedSmells;
    private final int[] refactoredSmells;
    private final int[] deletedSmells;

    public CommitOutput(int commitNumber, String sha, String developer, String status) {
        this.commitNumber = commitNumber;
        this.sha = sha;
        this.developer = developer;
        this.status = status;

        introducedSmells = new int[NB_SMELLS];
        refactoredSmells = new int[NB_SMELLS];
        deletedSmells = new int[NB_SMELLS];
    }

    public void setSmellCount(String name, int introduced, int refactored, int deleted) {
        try {
            logger.trace("Setting values for smell (" + name + "): I-" + introduced + "/R-" + refactored);
            int offset = SmellCode.valueOf(name).offset;
            introducedSmells[offset] = introduced;
            refactoredSmells[offset] = refactored;
            deletedSmells[offset] = deleted;
        } catch (IllegalArgumentException e) {
            logger.warn("Could not parse smell name: " + name);
        }
    }

    /**
     * Output format is the following:
     * commitNb, commitSha, commitStatus,
     * D1S1I, D1S1R, D1S1d,
     * D1S2I, D1S2R, D1S2d,
     * ...,
     * DxSyI, DxSyR, DxSyd
     *
     * With
     * D = developer,
     * S = Smell,
     * I = number introduced,
     * R = number refactored.
     * d = number deleted
     *
     * @param devOffset
     * @param totalDev
     * @return
     */
    public List<String> prepareOutputLine(int devOffset, int totalDev) {
        List<String> result = new ArrayList<String>(3 + totalDev * NB_SMELLS);
        result.add(String.valueOf(commitNumber));
        result.add(sha);
        result.add(status);

        // We fill empty rows for each developer's smells introduced and refactored before us
        for (int i = 0; i < devOffset * NB_SMELLS * NB_COLUMN_PER_SMELL; i++) {
            result.add(EMPTY_CELL);
        }

        for (int i = 0; i < NB_SMELLS; i++) {
            logger.trace("Dev n°" + devOffset + " introduced " + introducedSmells[i] + " smells n°" + i + " (commit " + commitNumber + ")");
            logger.trace("Dev n°" + devOffset + " refactored " + refactoredSmells[i] + " smells n°" + i + " (commit " + commitNumber + ")");
            logger.trace("Dev n°" + devOffset + " refactored " + deletedSmells[i] + " smells n°" + i + " (commit " + commitNumber + ")");

            result.add(String.valueOf(introducedSmells[i]));
            result.add(String.valueOf(refactoredSmells[i]));
            result.add(String.valueOf(deletedSmells[i]));
        }

        // We fill empty rows for each developer's smells introduced and refactored after us
        for (int i = devOffset + 1; i < totalDev * NB_SMELLS * NB_COLUMN_PER_SMELL; i++) {
            result.add(EMPTY_CELL);
        }

        return result;
    }
}
