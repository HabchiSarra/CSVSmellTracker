package org.tandoori.metrics.calculator.writer;

import org.tandoori.metrics.calculator.processing.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Store a {@link Tuple} counting the number of introduces/refactored smells
 * for a given key, either a smell name or a developer ID.
 */
class SmellStore {
    private Map<String, Tuple<Integer, Integer>> smells;

    public SmellStore() {
        smells = new HashMap<>();
    }

    public void addSmells(String name, int introduced, int refactored) {
        Tuple<Integer, Integer> devSmells = smells.getOrDefault(name, new Tuple<>(0, 0));

        smells.put(name, new Tuple<>(
                devSmells.introduced + introduced,
                devSmells.refactored + refactored)
        );
    }

    public Tuple<Integer, Integer> get(String name) {
        return smells.getOrDefault(name, new Tuple<>(0, 0));
    }
}
