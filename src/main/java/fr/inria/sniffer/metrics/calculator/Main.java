package fr.inria.sniffer.metrics.calculator;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Class to call to start a calculation process.
 */
public class Main {
        public static void main(String[] args){
            SmellsCalculator calculator = new SmellsCalculator();
            CmdLineParser parser = new CmdLineParser(calculator);
            try {
                parser.parseArgument(args);
                calculator.generateReport();
            } catch (CmdLineException e) {
                parser.printUsage(System.err);
            }
        }
}
