package org.tandoori.metrics.calculator;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sarra on 20/07/17.
 */
public class SmellsCalculator {

    @Option(name="-i", handler = StringArrayOptionHandler.class, forbids = "-d",
            usage="List the files to open")
    public List<File> inputFiles;

    @Option(name = "-d", forbids = "-a",
            usage = "Give a directory containing reports files as .csv")
    public File inputDir;

    @Option(name="-o", required = true, usage="Set the output file")
    public File outputFile;

    public void generateReport() {
        List<File> smellsFile = getFiles();
        new SmellsProcessor(smellsFile, outputFile).process();
    }

    /**
     * Correctly parse the input in case of a directory to parse or a list of filename.
     *
     * @return The list
     */
    private List<File> getFiles() {
        if(inputDir != null){
            File[] files = inputDir.listFiles(new SmellFileFilter());
            return files != null ? Arrays.asList(files) : Collections.<File>emptyList();
        }
        return inputFiles != null ? inputFiles : Collections.<File>emptyList();
    }

    private class SmellFileFilter implements FilenameFilter {
        public boolean accept(File file, String s) {
            return s.toLowerCase().endsWith(".csv");
        }
    }
}
