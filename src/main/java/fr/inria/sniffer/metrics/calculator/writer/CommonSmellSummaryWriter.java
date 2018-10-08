package fr.inria.sniffer.metrics.calculator.writer;

import fr.inria.sniffer.metrics.calculator.DevelopersHandler;
import fr.inria.sniffer.metrics.calculator.processing.CommitSmell;
import fr.inria.sniffer.metrics.calculator.utils.CSVUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

public abstract class CommonSmellSummaryWriter {
    private static final Logger logger = LoggerFactory.getLogger(CommonSmellSummaryWriter.class.getName());

    static final String REFACTORED_KEY = "R";
    static final String INTRODUCED_KEY = "I";
    static final String DELETED_KEY = "D";
    DevelopersHandler devHandler;

    private final File outputCsvFile;

    public CommonSmellSummaryWriter(File outputFile, DevelopersHandler devHandler) {
        this.outputCsvFile = outputFile;
        this.devHandler = devHandler;
    }

    public void write(List<CommitSmell> smells) {
        FileWriter fileWriter = null;

        Collections.sort(smells);
        try {
            logger.info("Writing output file:" + outputCsvFile.getName());
            fileWriter = new FileWriter(outputCsvFile);
            writeHeaderLine(fileWriter);
            writeValues(smells, fileWriter);
        } catch (IOException e) {
            logger.error("Unable to open file " + outputCsvFile.getAbsolutePath() + ": " + e.getLocalizedMessage());
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    logger.error("Unable to close file " + outputCsvFile.getAbsolutePath() + ": " + e.getLocalizedMessage());
                }
            }
        }
    }

    private void writeHeaderLine(Writer fileWriter) {
        try {
            CSVUtils.writeLine(fileWriter, getHeaderLine());
        } catch (IOException e) {
            logger.warn("Unable to print header line: " + e.getLocalizedMessage());
        }
    }

    /**
     * Helper method printing the given line
     * and printing an error in case of {@link IOException}.
     *
     * @param fileWriter
     * @param line
     */
    static void printLine(Writer fileWriter, List<String> line) {
        try {
            CSVUtils.writeLine(fileWriter, line);
        } catch (IOException e) {
            logger.warn("Unable to print line (" + line + "): " + e.getLocalizedMessage());
        }
    }

    /**
     * Actual record of the output commits.
     *
     * @param commits
     * @param fileWriter
     */
    protected abstract void writeValues(List<CommitSmell> commits, FileWriter fileWriter);

    /**
     * Generates the header.
     *
     * @return
     */
    protected abstract List<String> getHeaderLine();
}
