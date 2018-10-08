package fr.inria.sniffer.metrics.calculator.processing;

import org.junit.Before;
import org.mockito.ArgumentCaptor;
import fr.inria.sniffer.metrics.calculator.DevelopersHandlerImpl;
import fr.inria.sniffer.metrics.calculator.processing.verification.HappySmellChecker;
import fr.inria.sniffer.metrics.calculator.writer.SmellWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;

abstract class SmellProcessorTestHelper {
    DevelopersHandlerImpl devHandler;
    ArgumentCaptor<List<CommitSmell>> smellsCaptor;
    SmellWriter smellsWriter;

    @Before
    public void setUp() throws Exception {
        smellsWriter = mock(SmellWriter.class);
        smellsCaptor = ArgumentCaptor.forClass(List.class);
        devHandler = new DevelopersHandlerImpl();
    }

    /**
     * Create a new {@link SmellsProcessor} for the given project from the test resources.
     * This method is using the devHandler field, so it must be used only once per test!
     *
     * @param projectName The project to analyse.
     * @return The created smells processor for this project.
     * @throws IOException If some files could not be found.
     */
    SmellsProcessor genProcessor(String projectName) throws IOException {
        URL smellDir = getClass().getResource("/smellParsing/" + projectName + "/smells/");
        List<File> entries = Arrays.asList(new File(smellDir.getFile()).listFiles());
        return new SmellsProcessor(entries, devHandler, getCommitInOrder(projectName), new HappySmellChecker());
    }

    private List<String> getCommitInOrder(String projectName) throws IOException {
        URL logs = getClass().getResource("/smellParsing/" + projectName + "/logFile");
        BufferedReader br = null;
        String line;
        List<String> commits = new ArrayList<>();

        try {
            br = new BufferedReader(new FileReader(logs.getFile()));
            while ((line = br.readLine()) != null) {
                commits.add(line);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return commits;
    }

    int countAnalyzed(List<CommitSmell> smells) {
        Set<Integer> commitsNb = new HashSet<>();
        smells.forEach(commitSmell -> commitsNb.add(commitSmell.commitNumber));
        return commitsNb.size();
    }

    int countNoDeps(List<CommitSmell> smells) {
        Set<Integer> commitsNb = new HashSet<>();
        int noDepsNb = 0;
        for (CommitSmell smell : smells) {
            if (smell.status.equals("no_dependencies") && !commitsNb.contains(smell.commitNumber)) {
                noDepsNb++;
                commitsNb.add(smell.commitNumber);
            }
        }
        return noDepsNb;
    }
}
