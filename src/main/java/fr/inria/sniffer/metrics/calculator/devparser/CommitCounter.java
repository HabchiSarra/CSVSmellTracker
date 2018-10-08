package fr.inria.sniffer.metrics.calculator.devparser;

class CommitCounter {
    /**
     * Total number of commits the in the project.
     */
    private int totalCommits;

    /**
     * Number of analyzed commits the developer in the project.
     */
    private int analyzedCommits;

    void addCommit(boolean parsed) {
        totalCommits++;
        if (parsed) {
            analyzedCommits++;
        }
    }

    public int getAnalyzedCommits() {
        return analyzedCommits;
    }

    public int getTotalCommits() {
        return totalCommits;
    }
}
