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
package fr.inria.sniffer.metrics.calculator.writer;

import fr.inria.sniffer.metrics.calculator.processing.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Store a {@link Tuple} counting the number of introduces/refactored smells
 * for a given key, either a smell name or a developer ID.
 */
public class SmellStore {
    private Map<String, Tuple<Integer, Integer, Integer>> smells;

    public SmellStore() {
        smells = new HashMap<>();
    }

    public void addSmells(String name, int introduced, int refactored, int deleted) {
        Tuple<Integer, Integer, Integer> devSmells = smells.getOrDefault(name, new Tuple<>(0, 0, 0));

        smells.put(name, new Tuple<>(
                        devSmells.introduced + introduced,
                        devSmells.refactored + refactored,
                        devSmells.deleted + deleted
                )
        );
    }

    public Tuple<Integer, Integer, Integer> get(String name) {
        return smells.getOrDefault(name, new Tuple<>(0, 0, 0));
    }
}
