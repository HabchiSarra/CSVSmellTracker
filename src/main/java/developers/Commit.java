package developers;

public class Commit {

    public int commitNumber;
    public String sha;
    public String developer;
    public int introducedSmells;
    public int refactoredSmells;

    public Commit(int commitNumber, String sha, String developer) {
        this.commitNumber = commitNumber;
        this.sha = sha;
        this.developer = developer;
    }

    public void setSmells(Tuple<Integer, Integer> smells) {
        this.introducedSmells = smells.introduced;
        this.refactoredSmells = smells.refactored;
    }
}
