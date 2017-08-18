package org.tandoori.metrics.calculator.devparser;

import org.tandoori.metrics.calculator.processing.Tuple;
import org.tandoori.metrics.calculator.writer.SmellStore;

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
