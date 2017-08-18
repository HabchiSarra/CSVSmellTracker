package org.tandoori.metrics.calculator.processing.verification;

import org.tandoori.metrics.calculator.SmellCode;

/**
 * This implementation always consider the changes as refactoring.
 */
public class HappySmellChecker implements SmellChecker {
    @Override
    public boolean isActualRefactoring(SmellCode code, String instance, String commitHash) {
        return true;
    }
}
