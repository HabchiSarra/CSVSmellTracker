package org.tandoori.metrics.calculator;

/**
 * Describe available smells
 * and their offset in the output CSV file.
 */
public enum SmellCode {
     HMU(0),
     IGS(1),
     IOD(2),
     IWR(3),
     LIC(4),
     MIM(5),
     NLMR(6),
     UCS(7),
     UHA(8),
     UIO(9);

    public final int offset;

    SmellCode(int offset) {
        this.offset = offset;
    }
}
