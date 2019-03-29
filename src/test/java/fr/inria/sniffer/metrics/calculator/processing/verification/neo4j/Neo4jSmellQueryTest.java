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
package fr.inria.sniffer.metrics.calculator.processing.verification.neo4j;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;

public class Neo4jSmellQueryTest {
    private static final String EXISTING_INSTANCE = "existingInstance";
    private static final String EXISTING_COMMIT = "existingCommit";
    private SmellQuery query;

    @Before
    public void setUp() throws Exception {
        File db = new File(getClass().getResource("/neo4jSmellQueryTest/databases/graph.db").getFile());
        query = new SmellQuery(db);
    }

    @After
    public void tearDown() throws Exception {
        query.shutDown();
    }

    @Test
    public void testClassSmellFoundReturnsTrue() throws Exception {
        boolean foundInstance = query.isClassExisting(EXISTING_COMMIT, EXISTING_INSTANCE);

        Assert.assertThat("The Class instance is found at the given commit", foundInstance, is(true));
    }

    @Test
    public void notExistingClassSmellReturnsFalse() throws Exception {
        boolean foundInstance = query.isClassExisting("otherCommit", EXISTING_INSTANCE);

        Assert.assertThat("The Class instance is not found at the wrong commit", foundInstance, is(false));
    }

    @Test
    public void testMethodSmellFoundReturnsTrue() throws Exception {
        boolean foundInstance = query.isMethodExisting(EXISTING_COMMIT, EXISTING_INSTANCE);

        Assert.assertThat("The Method instance is found at the given commit", foundInstance, is(true));
    }

    @Test
    public void notExistingMethodSmellReturnsFalse() throws Exception {
        boolean foundInstance = query.isMethodExisting("otherCommit", EXISTING_INSTANCE);

        Assert.assertThat("The Method instance is not found at the wrong commit", foundInstance, is(false));
    }
}