package org.tandoori.metrics.calculator.processing.verification;

import org.tandoori.metrics.calculator.SmellCode;

import java.io.File;

public class Neo4jSmellChecker implements SmellChecker {
    private File dbPath;

    public Neo4jSmellChecker(File dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public boolean isActualRefactoring(SmellCode code, String instance, String commitHash) {
        switch (code) {
            // TODO: Identify the smells to sanity check.
            case HMU:
            case IGS:
            case IOD:
            case IWR:
            case LIC:
            case MIM:
            case NLMR:
            case UCS:
            case UHA:
            case UIO:
                return true;
        }
        return false;
    }
}
