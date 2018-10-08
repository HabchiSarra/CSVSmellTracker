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
