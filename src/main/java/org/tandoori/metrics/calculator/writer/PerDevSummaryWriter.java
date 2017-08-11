package org.tandoori.metrics.calculator.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tandoori.metrics.calculator.DevelopersHandler;
import org.tandoori.metrics.calculator.devparser.DevParser;
import org.tandoori.metrics.calculator.devparser.Developer;
import org.tandoori.metrics.calculator.devparser.Project;
import org.tandoori.metrics.calculator.processing.CommitSmell;
import org.tandoori.metrics.calculator.processing.Tuple;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.tandoori.metrics.calculator.processing.SmellsProcessor.NO_SMELL_CODE;

/**
 * Count the overall smells introduction and refactoring per developer
 * The output will be of the form
 *
 * project, dev_id, name, email, I, R, ratio_I, ratio_R,
 * nb_commits_analyzed, ratio_Ca, ratio_I_Ca, ratio_R_Ca,
 * nb_commits_project, ratio_Cp, ratio_I_Cp, ratio_R_Cp
 *
 * Where:
 * I = Introduced smell
 * R = Refactored smell
 * Ca = analyzed commits
 * Cp = commits in projects
 */
public class PerDevSummaryWriter extends CommonSmellSummaryWriter implements SmellWriter {
    private static final Logger logger = LoggerFactory.getLogger(PerDevSummaryWriter.class.getName());
    private static final String PROJECT_NAME = "project";
    private static final String DEV_ID = "dev_id";
    private static final String DEV_NAME = "name";
    private static final String DEV_EMAIL = "email";
    private static final String RATIO_I = "ratio_I";
    private static final String RATIO_R = "ratio_R";
    private static final String ANALYZED_COMMITS = "commits_analyzed";
    private static final String RATIO_ANALYZED_COMMITS = "ratio_Ca";
    private static final String RATIO_I_CA = "ratio_I_Ca";
    private static final String RATIO_R_CA = "ratio_R_Ca";
    private static final String PROJECT_COMMITS = "nb_commits_project";
    private static final String RATIO_PROJECT_COMMITS = "ratio_Cp";
    private static final String RATIO_I_CP = "ratio_I_Cp";
    private static final String RATIO_R_CP = "ratio_R_Cp";

    /**
     * Project identifier to write if nothing is set.
     */
    private static final String DEFAULt_PROJECT_NAME = "unknown_project";

    /**
     * Project identifier to set on the output.
     */
    private final String projectName;

    /**
     * {@link SmellStore} sorted by developer.
     */
    private SmellStore devIntroducedRefactored;

    /**
     * File containing all the commits for the given project.
     */
    private final File projectCommits;

    /**
     * Build a new {@link PerDevSummaryWriter} object.
     *
     * @param outputFile     The file in which the results should be put.
     * @param devHandler     Handler containing the data of analyzed devs
     * @param projectCommits File containing the output of GitMiner.
     * @param projectName    Name of the project.
     */
    public PerDevSummaryWriter(File outputFile, DevelopersHandler devHandler, File projectCommits, String projectName) {
        super(outputFile, devHandler);
        this.projectCommits = projectCommits;
        devIntroducedRefactored = new SmellStore();
        this.projectName = projectName != null ? projectName : DEFAULt_PROJECT_NAME;
    }

    @Override
    protected void writeValues(List<CommitSmell> commits, FileWriter fileWriter) {
        DevParser devParser = parseDevelopersInput(commits);

        int totalI = 0;
        int totalR = 0;

        int firstCommitNb = 0;
        if (!commits.isEmpty()) {
            firstCommitNb = commits.get(0).commitNumber;
        } else {
            logger.warn("No commits provided, output file will be empty");
        }
        for (CommitSmell commit : commits) {
            // We won't count the smells introduced in the first commit
            // since it will contain every smells in the code so far.
            if (commit.commitNumber == firstCommitNb) {
                continue;
            }
            if (!commit.smellName.equals(NO_SMELL_CODE)) {
                totalI += commit.introduced();
                totalR += commit.refactored();
                devIntroducedRefactored.addSmells(commit.developer, commit.introduced(), commit.refactored());
            }
        }

        Project project = devParser.getProject();
        Map<String, Developer> developers = devParser.getDevelopers();
        String[] devs = devHandler.sortedDevelopers();
        for (String devId : devs) {
            if (!developers.containsKey(devId)) {
                logger.error("Developer " + devId + " not found in commit list, an error occurred!");
                continue;
            }
            writeCommitLine(fileWriter, devId, project, developers, totalI, totalR);
        }
    }

    private DevParser parseDevelopersInput(List<CommitSmell> commits) {
        Set<String> analyzedCommits = new HashSet<>();
        for (CommitSmell commit : commits) {
            analyzedCommits.add(commit.sha);
        }
        DevParser devParser = new DevParser(projectCommits, analyzedCommits);
        devParser.parse();
        return devParser;
    }

    private void writeCommitLine(FileWriter fileWriter, String devId, Project project,
                                 Map<String, Developer> developers, int totalI, int totalR) {
        Developer dev = developers.get(devId);
        Tuple<Integer, Integer> smells = devIntroducedRefactored.get(devId);
        printLine(fileWriter, getContentLine(project, dev, smells, totalI, totalR));
    }

    @Override
    protected List<String> getHeaderLine() {
        List<String> header = new ArrayList<>();
        header.add(PROJECT_NAME);
        header.add(DEV_ID);
        header.add(DEV_NAME);
        header.add(DEV_EMAIL);

        header.add(INTRODUCED_KEY);
        header.add(REFACTORED_KEY);
        header.add(RATIO_I);
        header.add(RATIO_R);

        header.add(ANALYZED_COMMITS);
        header.add(RATIO_ANALYZED_COMMITS);
        header.add(RATIO_I_CA);
        header.add(RATIO_R_CA);

        header.add(PROJECT_COMMITS);
        header.add(RATIO_PROJECT_COMMITS);
        header.add(RATIO_I_CP);
        header.add(RATIO_R_CP);
        return header;
    }

    private List<String> getContentLine(Project project, Developer dev, Tuple<Integer, Integer> smells,
                                        int totalI, int totalR) {
        List<String> line = new ArrayList<>();
        line.add(projectName);
        line.add(dev.id);
        line.add(dev.name);
        line.add(dev.email);

        line.add(String.valueOf(smells.introduced));
        line.add(String.valueOf(smells.refactored));
        float ratioI = smells.introduced / (float) totalI;
        line.add(String.valueOf(ratioI));
        float ratioR = smells.refactored / (float) totalR;
        line.add(String.valueOf(ratioR));

        addCommitsRatio(line, dev.getAnalyzedCommits(), project.getAnalyzedCommits(), ratioI, ratioR);

        addCommitsRatio(line, dev.getTotalCommits(), project.getTotalCommits(), ratioI, ratioR);

        return line;
    }

    private void addCommitsRatio(List<String> line, int devCommits, float projectCommits, float ratioI, float ratioR) {
        float commitRatio = devCommits / projectCommits;
        line.add(String.valueOf(devCommits));
        line.add(String.valueOf(commitRatio));
        line.add(String.valueOf(ratioI / commitRatio));
        line.add(String.valueOf(ratioR / commitRatio));
    }
}
