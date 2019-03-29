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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class SmellsProcessorTest extends SmellProcessorTestHelper {
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void processYaaic() throws Exception {
        checkProject("Yaaic", 114, 0);
    }

    @Test
    public void processOrWall() throws Exception {
        // FIXME: Those minus 13 commits are commits that are buggy because of Spoon in Paprika, See: https://github.com/HabchiSarra/SmellDetector/issues/8
        checkProject("orWall", 595 - 13, 429 - 13);
    }

    @Test
    public void processSigfood() throws Exception {
        checkProject("sigfood", 7, 0);
    }

    @Test
    public void processClover() throws Exception {
        // FIXME: This minus 1 commit is because of Spoon in Paprika, See: https://github.com/HabchiSarra/SmellDetector/issues/8
        checkProject("Clover", 904 - 1, 28);
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
}