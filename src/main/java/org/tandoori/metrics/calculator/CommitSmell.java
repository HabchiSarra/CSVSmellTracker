package org.tandoori.metrics.calculator;

/**
 * Describe the additions and deletions of a specific smell
 * in the described commit.
 *
 * This commit is associated to a unique developer.
 */
public class CommitSmell implements Comparable<CommitSmell> {
    public final String smellName;
    public final int commitNumber;
    public final String sha;
    public final String developer;
    public final String status;

    private int introducedSmells;
    private int refactoredSmells;

    public int introduced() {
        return introducedSmells;
    }

    public int refactored() {
        return refactoredSmells;
    }

    public CommitSmell(String smellName, int commitNumber, String sha, String status, String developer) {
        this.smellName = smellName;
        this.commitNumber = commitNumber;
        this.sha = sha;
        this.status = status;
        this.developer = developer;
    }

    public void setSmells(Tuple<Integer, Integer> smells) {
        this.introducedSmells = smells.introduced;
        this.refactoredSmells = smells.refactored;
    }

    public int compareTo(CommitSmell commitSmell) {
        return Integer.valueOf(this.commitNumber)
                .compareTo(commitSmell.commitNumber);
    }
}
