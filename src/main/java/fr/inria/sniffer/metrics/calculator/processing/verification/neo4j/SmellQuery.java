/*
 * Paprika - Detection of code smells in Android application
 *     Copyright (C)  2016  Geoffrey Hecht - INRIA - UQAM - University of Lille
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.inria.sniffer.metrics.calculator.processing.verification.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SmellQuery {
    private static final Logger logger = LoggerFactory.getLogger(SmellQuery.class.getName());

    private GraphDatabaseService graphDatabaseService;
    private DatabaseManager databaseManager;

    public GraphDatabaseService getGraphDatabaseService() {
        return graphDatabaseService;
    }

    SmellQuery(File DatabasePath) {
        this.databaseManager = new DatabaseManager(DatabasePath);
        databaseManager.start();
        graphDatabaseService = databaseManager.getGraphDatabaseService();
    }

    public void shutDown() {
        databaseManager.shutDown();
    }

    public boolean isClassExisting(String commitHash, String identifier) {
        return isExisting(commitHash, identifier, "Class", "name");
    }

    public boolean isMethodExisting(String commitHash, String identifier) {
        return isExisting(commitHash, identifier, "Method", "full_name");
    }

    private boolean isExisting(String commitHash, String identifier, String type, String nameKey) {
        logger.debug("Querying " + type + " of commit " + commitHash + " for smell " + identifier);
        Result result = graphDatabaseService.execute(
                "MATCH (n:" + type + ") WHERE " +
                        "n.app_key='" + commitHash + "' AND n." + nameKey + "='" + identifier + "' " +
                        "RETURN n");
        boolean exists = result.hasNext();
        result.close();
        logger.debug("Is " + type + " existing? " + exists);
        return exists;
    }
}
