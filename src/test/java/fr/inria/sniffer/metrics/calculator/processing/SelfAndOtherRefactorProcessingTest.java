package fr.inria.sniffer.metrics.calculator.processing;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class SelfAndOtherRefactorProcessingTest extends SmellProcessorTestHelper {
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testCloverRefactoring() throws Exception {
        checkRefactoredValues("Clover");
    }

    @Test
    public void testSigfoodRefactoring() throws Exception {
        checkRefactoredValues("sigfood");
    }

    @Test
    public void testorWallRefactoring() throws Exception {
        checkRefactoredValues("orWall");
    }

    @Test
    public void testYaaicRefactoring() throws Exception {
        checkRefactoredValues("Yaaic");
    }

    private void checkRefactoredValues(String project) throws IOException {
        SmellsProcessor processor = genProcessor(project);
        processor.addOutput(smellsWriter);

        processor.process();
        verify(smellsWriter).write(smellsCaptor.capture());

        for (String dev : devHandler.sortedDevelopers()) {
            List<CommitSmell> smells = smellsCaptor.getValue();
            assertThat("Dev " + dev + " has as many refactored smells from CommitSmells and DevHandler",
                    devHandler.countRefactored(dev), is(countRefactorForDev(smells, dev)));

        }
    }

    private long countRefactorForDev(List<CommitSmell> smells, String devId) {
        long nbRefactor = 0;
        for (CommitSmell smell : smells) {
            if (smell.developer.equals(devId) && !smell.smellName.equals(SmellsProcessor.NO_SMELL_CODE)) {
                nbRefactor += smell.refactored();
            }
        }
        return nbRefactor;
    }
}