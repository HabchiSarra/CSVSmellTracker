package fr.inria.sniffer.metrics.calculator.writer;

import fr.inria.sniffer.metrics.calculator.SmellCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.inria.sniffer.metrics.calculator.DevelopersHandler;
import fr.inria.sniffer.metrics.calculator.devparser.DevParser;
import fr.inria.sniffer.metrics.calculator.devparser.Developer;
import fr.inria.sniffer.metrics.calculator.devparser.Project;
import fr.inria.sniffer.metrics.calculator.processing.CommitSmell;
import fr.inria.sniffer.metrics.calculator.processing.Tuple;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.inria.sniffer.metrics.calculator.processing.SmellsProcessor.NO_SMELL_CODE;

/**
 * Count the overall smells introduction and refactoring per developer
 * The output will be of the form
 *
 * project, dev_id, name, email, I, R, self_smells_R, other_dev_smells_R ratio_I, ratio_R,
 * nb_commits_analyzed, ratio_Ca, ratio_I_Ca, ratio_R_Ca,
 * nb_commits_project, ratio_Cp, ratio_I_Cp, ratio_R_Cp,
 * S1_I, S1_R, ..., Sn_I, Sn_R
 *
 * Where:
 * I = Introduced smell
 * R = Refactored smell
 * Ca = analyzed commits
 * Cp = commits in projects
 * Sn = Smell n
 */
public class PerDevSummaryWriter extends CommonSmellSummaryWriter implements SmellWriter {
    private static final Logger logger = LoggerFactory.getLogger(PerDevSummaryWriter.class.getName());
    private static final String PROJECT_NAME = "project";
    private static final String DEV_ID = "dev_id";
    private static final String DEV_NAME = "name";
    private static final String DEV_EMAIL = "email";
    private static final String REFACTORED_SELF = "self_smells_R";
    private static final String REFACTORED_OTHER = "other_dev_smells_R";
    private static final String DELETED_SELF = "self_smells_D";
    private static final String DELETED_OTHER = "other_dev_smells_D";
    private static final String RATIO_I = "ratio_I";
    private static final String RATIO_R = "ratio_R";
    private static final String RATIO_D = "ratio_D";
    private static final String ANALYZED_COMMITS = "commits_analyzed";
    private static final String RATIO_ANALYZED_COMMITS = "ratio_Ca";
    private static final String RATIO_I_CA = "ratio_I_Ca";
    private static final String RATIO_R_CA = "ratio_R_Ca";
    private static final String RATIO_D_CA = "ratio_D_Ca";
    private static final String PROJECT_COMMITS = "nb_commits_project";
    private static final String RATIO_PROJECT_COMMITS = "ratio_Cp";
    private static final String RATIO_I_CP = "ratio_I_Cp";
    private static final String RATIO_R_CP = "ratio_R_Cp";
    private static final String RATIO_D_CP = "ratio_D_Cp";

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
                devIntroducedRefactored.addSmells(commit.developer,
                        commit.introduced(), commit.refactored(), commit.deleted());

                Developer developer = devParser.getDevelopers().get(commit.developer);
                developer.updateSmell(commit.smellName,
                        commit.introduced(), commit.refactored(), commit.deleted());
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
        Tuple<Integer, Integer, Integer> smells = devIntroducedRefactored.get(devId);
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
        header.add(DELETED_KEY);
        header.add(REFACTORED_SELF);
        header.add(REFACTORED_OTHER);
        header.add(DELETED_SELF);
        header.add(DELETED_OTHER);
        header.add(RATIO_I);
        header.add(RATIO_R);
        header.add(RATIO_D);

        header.add(ANALYZED_COMMITS);
        header.add(RATIO_ANALYZED_COMMITS);
        header.add(RATIO_I_CA);
        header.add(RATIO_R_CA);
        header.add(RATIO_D_CA);

        header.add(PROJECT_COMMITS);
        header.add(RATIO_PROJECT_COMMITS);
        header.add(RATIO_I_CP);
        header.add(RATIO_R_CP);
        header.add(RATIO_D_CP);

        for (SmellCode smellCode : SmellCode.values()) {
            header.add(smellCode.name() + "_" + INTRODUCED_KEY);
            header.add(smellCode.name() + "_" + REFACTORED_KEY);
            header.add(smellCode.name() + "_" + DELETED_KEY);
        }

        return header;
    }

    private List<String> getContentLine(Project project, Developer dev, Tuple<Integer, Integer, Integer> smells,
                                        int totalI, int totalR) {
        List<String> line = new ArrayList<>();
        line.add(projectName);
        line.add(dev.id);
        line.add(dev.name);
        line.add(dev.email);

        line.add(String.valueOf(smells.introduced));
        line.add(String.valueOf(smells.refactored));
        line.add(String.valueOf(smells.deleted));
        line.add(String.valueOf(devHandler.countSelfRefactored(dev.id)));
        line.add(String.valueOf(devHandler.countOtherRefactored(dev.id)));
        line.add(String.valueOf(devHandler.countSelfDeleted(dev.id)));
        line.add(String.valueOf(devHandler.countOtherDeleted(dev.id)));

        float ratioI = smells.introduced / (float) totalI;
        line.add(String.valueOf(ratioI));
        float ratioR = smells.refactored / (float) totalR;
        line.add(String.valueOf(ratioR));
        float ratioD = smells.deleted / (float) totalR;
        line.add(String.valueOf(ratioD));

        addCommitsRatio(line, dev.getAnalyzedCommits(), project.getAnalyzedCommits(), ratioI, ratioR, ratioD);

        addCommitsRatio(line, dev.getTotalCommits(), project.getTotalCommits(), ratioI, ratioR, ratioD);

        // Ensure that the iteration is in the same order as in Header line
        Tuple<Integer, Integer, Integer> count;
        for (SmellCode smellCode : SmellCode.values()) {
            count = dev.getSmellCounts(smellCode.name());
            line.add(String.valueOf(count.introduced));
            line.add(String.valueOf(count.refactored));
            line.add(String.valueOf(count.deleted));
        }

        return line;
    }

    private void addCommitsRatio(List<String> line, int devCommits, float projectCommits,
                                 float ratioI, float ratioR, float ratioD) {
        float commitRatio = devCommits / projectCommits;
        line.add(String.valueOf(devCommits));
        line.add(String.valueOf(commitRatio));
        line.add(String.valueOf(ratioI / commitRatio));
        line.add(String.valueOf(ratioR / commitRatio));
        line.add(String.valueOf(ratioD / commitRatio));
    }
}
