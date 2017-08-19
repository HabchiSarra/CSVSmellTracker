package org.tandoori.metrics.calculator.processing.verification.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;


class DatabaseManager {
    private final File dbPath;
    private GraphDatabaseService graphDatabaseService;

    DatabaseManager(File dbPath) {
        this.dbPath = dbPath;
    }

    void start() {
        graphDatabaseService = new GraphDatabaseFactory().
                newEmbeddedDatabaseBuilder(dbPath).
                newGraphDatabase();
        registerShutdownHook(graphDatabaseService);
    }

    void shutDown() {
        graphDatabaseService.shutdown();
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

    GraphDatabaseService getGraphDatabaseService() {
        return graphDatabaseService;
    }
}
