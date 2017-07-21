package org.tandoori.metrics.calculator;

import java.util.List;

/**
 * Created by sarra on 18/07/17.
 */
public interface DevelopersHandler {
    void notify(String developer);

    /**
     * Returns the number of synchronized developers.
     *
     * @return The number of developers
     */
    int size();

    /**
     * Returns the developer offset.
     *
     * @return Offset for the given developer.
     */
    int getOffset(String devName);

    /**
     * Returns an array containing the developers in the right offset order.
     *
     * @return An array of developers
     */
    String[] sortedDevelopers();
}
