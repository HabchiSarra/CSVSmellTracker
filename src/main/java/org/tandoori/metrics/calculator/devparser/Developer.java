package org.tandoori.metrics.calculator.devparser;

/**
 * Developer of a Project.
 */
public class Developer extends CommitCounter {
    public final String id;
    public final String name;
    public final String email;

    Developer(String id, String name, String email) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
