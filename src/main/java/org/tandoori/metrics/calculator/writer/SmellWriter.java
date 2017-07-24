package org.tandoori.metrics.calculator.writer;

import org.tandoori.metrics.calculator.processing.CommitSmell;

import java.io.FileWriter;
import java.util.List;

/**
 * Common interface of a smell summary writer.
 */
public interface SmellWriter {

    /**
     * Handles the {@link FileWriter} opening and record the values.
     *
     * @param smells The analyzed smells to write out.
     */
    void write(List<CommitSmell> smells);
}
