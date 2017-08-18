package org.tandoori.metrics.calculator.processing.verification;

import org.tandoori.metrics.calculator.SmellCode;

/**
 * Performs some checks on smells.
 */
public interface SmellChecker {
    /**
     * Verify if the smell has been refactored or simply removed from the source code.
     *
     * @param code       The smell code name.
     * @param instance   The smell instance definition.
     * @param commitHash The commit in which this smell has supposedly been refactored.
     * @return true if the smell has disappeared after an actual refactoring,
     * false if the source containing this smells has simply been wiped.
     */
    boolean isActualRefactoring(SmellCode code, String instance, String commitHash);
}
