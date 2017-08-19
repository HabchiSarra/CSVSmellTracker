package org.tandoori.metrics.calculator.processing.verification.neo4j;

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