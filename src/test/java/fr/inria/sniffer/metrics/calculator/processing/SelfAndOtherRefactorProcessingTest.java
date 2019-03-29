/**
 *   Sniffer - Analyze the history of Android code smells at scale.
 *   Copyright (C) 2019 Sarra Habchi
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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