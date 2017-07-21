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

    public final int commitNumber;
    public final String sha;
    public final String developer;
    public final String status;

    private static final String EMPTY_CELL = "0";
    private static final int NB_SMELLS = SmellCode.values().length;

    private final int[] introducedSmells;
    private final int[] refactoredSmells;

    public CommitOutput(int commitNumber, String sha, String developer, String status) {
        this.commitNumber = commitNumber;
        this.sha = sha;
        this.developer = developer;
        this.status = status;

        introducedSmells = new int[NB_SMELLS];
        refactoredSmells = new int[NB_SMELLS];
    }

    public void setSmellCount(String name, int introduced, int refactored) {
        try {
            int offset = SmellCode.valueOf(name).offset;
            introducedSmells[offset] = introduced;
            introducedSmells[offset] = refactored;
        } catch (IllegalArgumentException e) {
            logger.warn("Could not parse smell name: " + name);
        }
    }

    /**
     * Output format is the following:
     * commitNb, commitSha, commitStatus, D1S1I, D1S1R, D1S2I, D1S2R,..., DxSyI, DxSyR
     *
     * With
     * D = developer,
     * S = Smell,
     * I = number introduced,
     * R = number refactored.
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
        for (int i = 0; i < devOffset * NB_SMELLS * 2; i++) {
            result.add(EMPTY_CELL);
        }

        for (int i = 0; i < NB_SMELLS; i++) {
            result.add(String.valueOf(introducedSmells[i]));
            result.add(String.valueOf(refactoredSmells[i]));
        }

        // We fill empty rows for each developer's smells introduced and refactored after us
        for (int i = devOffset + 1; i < totalDev * NB_SMELLS * 2; i++) {
            result.add(EMPTY_CELL);
        }

        return result;
    }
}
