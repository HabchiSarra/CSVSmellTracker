package org.tandoori.metrics.calculator.processing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.tandoori.metrics.calculator.DevelopersHandlerImpl;
import org.tandoori.metrics.calculator.processing.verification.HappySmellChecker;
import org.tandoori.metrics.calculator.writer.SmellWriter;

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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SmellsProcessorTest {
    private ArgumentCaptor<List<CommitSmell>> smellsCaptor;
    private SmellWriter smellsWriter;

    @Before
    public void setUp() throws Exception {
        smellsWriter = mock(SmellWriter.class);
        smellsCaptor = ArgumentCaptor.forClass(List.class);
    }

    @Test
    public void processYaaic() throws Exception {
        checkProject("Yaaic", 114, 0);
    }

    @Test
    public void processOrWall() throws Exception {
        // FIXME: Those minus 13 commits are commits that are buggy because of Spoon in Paprika, See: https://github.com/HSarah/Tandoori/issues/8
        checkProject("orWall", 595 - 13, 429 - 13);
    }

    @Test
    public void processSigfood() throws Exception {
        checkProject("sigfood", 7, 0);
    }

    private void checkProject(String name, int analyzedCommits, int noDepsCommits) throws IOException {
        SmellsProcessor processor = genProcessor(name);
        processor.addOutput(smellsWriter);

        processor.process();
        verify(smellsWriter).write(smellsCaptor.capture());

        // We should see the same number of commit as the number of analyzed commits.
        List<CommitSmell> smells = smellsCaptor.getValue();
        assertThat("We have enough analyzed commits", countAnalyzed(smells), is(analyzedCommits));
        assertThat("We have enough nodeps commits", countNoDeps(smells), is(noDepsCommits));
    }

    private SmellsProcessor genProcessor(String projectName) throws IOException {
        URL smellDir = getClass().getResource("/smellParsing/" + projectName + "/smells/");
        List<File> entries = Arrays.asList(new File(smellDir.getFile()).listFiles());
        return new SmellsProcessor(entries, new DevelopersHandlerImpl(), getCommitInOrder(projectName), new HappySmellChecker());
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

    private int countAnalyzed(List<CommitSmell> smells) {
        Set<Integer> commitsNb = new HashSet<>();
        smells.forEach(commitSmell -> commitsNb.add(commitSmell.commitNumber));
        return commitsNb.size();
    }

    private int countNoDeps(List<CommitSmell> smells) {
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