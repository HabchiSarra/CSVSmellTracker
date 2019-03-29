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

import fr.inria.sniffer.metrics.calculator.SmellCode;
import fr.inria.sniffer.metrics.calculator.processing.verification.SmellChecker;

import java.io.File;

public class Neo4jSmellChecker implements SmellChecker {
    private final SmellQuery smellQuery;

    public Neo4jSmellChecker(File dbPath) {
        smellQuery = new SmellQuery(dbPath);
    }

    @Override
    public boolean isActualRefactoring(SmellCode code, String instance, String commitHash) {
        switch (code) {
            case HMU:
            case IOD:
            case IWR:
            case MIM:
            case UCS:
            case UHA:
            case UIO:
                return smellQuery.isMethodExisting(commitHash, instance);
            case LIC:
            case NLMR:
                return smellQuery.isClassExisting(commitHash, instance);
        }
        return false;
    }
}
