package developers;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmellsProcessor implements DeveloperSet {
    private final Map<String, Integer> developersCode = new HashMap<String, Integer>();
    private static final Map<String, Integer> SMELL_CODE;

    static {
        SMELL_CODE = new HashMap<String, Integer>();
        SMELL_CODE.put("HMU", 0);
        SMELL_CODE.put("IGS", 1);
        SMELL_CODE.put("IOD", 2);
        SMELL_CODE.put("IWR", 3);
        SMELL_CODE.put("LIC", 4);
        SMELL_CODE.put("MIM", 5);
        SMELL_CODE.put("NLMR", 6);
        SMELL_CODE.put("UCS", 7);
        SMELL_CODE.put("UHA", 8);
        SMELL_CODE.put("UIO", 9);
    }

    private List<File> smellFiles;
    private final File outputCsvFile;

    public SmellsProcessor(List<File> smellFiles, File outputCsvFile) {
        this.smellFiles = smellFiles;
        this.outputCsvFile = outputCsvFile;
    }

    public void process() {
        SmellProcessor processor;
        String smellName;
        for (File smellFile : smellFiles) {
            smellName = getSmellName(smellFile);
            processor = new SmellProcessor(smellFile, this);
            record(SMELL_CODE.get(smellName), processor.process());
        }
    }

    /**
     * We consider the raw filename from Paprika e.g. 2017_7_18_11_25_HMU.csv
     *
     * @param smellFile
     * @return
     */
    private String getSmellName(File smellFile) {
        String[] split = smellFile.getName().split("_");
        return split[split.length - 1].split(".")[0];
    }

    public void record(int smellPosition, List<Commit> commits) {
        // TODO: Write to outputCsvFile
        //SMELL_CODE.get(smellName)
    }

    public void notify(String developer) {
        if (!developersCode.containsKey(developer)) {
            developersCode.put(developer, developersCode.size() + 1);
        }
    }
}
