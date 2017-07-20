package developers;

/**
 * Parse a Tandoori smell analysis CSV line.
 *
 * We are expecting data of the form:
 * commitNumber, commitSha, smellInstance, developer, commitStatus
 */
public class InputSmell {
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
            System.err.println("Unable to parse smell input: " + line);
        }
        return new InputSmell(content[2], Integer.valueOf(content[0]), content[1], content[2], content[2]);
    }
}
