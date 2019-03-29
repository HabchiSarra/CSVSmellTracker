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
package fr.inria.sniffer.metrics.calculator.devparser;

import fr.inria.sniffer.metrics.calculator.writer.SmellStore;
import fr.inria.sniffer.metrics.calculator.processing.Tuple;

/**
 * Developer of a Project.
 */
public class Developer extends CommitCounter {
    public final String id;
    public final String name;
    public final String email;

    private final SmellStore smellsByName;

    Developer(String id, String name, String email) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        smellsByName = new SmellStore();
    }

    public void updateSmell(String name, int introduced, int refactored, int deleted) {
        smellsByName.addSmells(name, introduced, refactored, deleted);
    }

    public Tuple<Integer, Integer, Integer> getSmellCounts(String name) {
        return smellsByName.get(name);
    }
}
