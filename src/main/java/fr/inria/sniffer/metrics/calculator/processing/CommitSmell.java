/**
 *   Sniffer - Analyze the history of Android code smells at scale.
 *   Copyright (C) 2019 Sarra Habchi
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.inria.sniffer.metrics.calculator.processing;

/**
 * Describe the additions and deletions of a specific smell
 * in the described commit.
 *
 * This commit is associated to a unique developer.
 */
public class CommitSmell implements Comparable<CommitSmell> {
    public final String smellName;
    public final int commitNumber;
    public final String sha;
    public final String developer;
    public final String status;

    private int introducedSmells;
    private int refactoredSmells;
    private int deletedSmells;

    public int introduced() {
        return introducedSmells;
    }

    public int refactored() {
        return refactoredSmells;
    }

    public int deleted() {
        return deletedSmells;
    }

    public CommitSmell(String smellName, int commitNumber, String sha, String status, String developer) {
        this.smellName = smellName;
        this.commitNumber = commitNumber;
        this.sha = sha;
        this.status = status;
        this.developer = developer;
    }

    public void setSmells(Tuple<Integer, Integer, Integer> smells) {
        this.introducedSmells = smells.introduced;
        this.refactoredSmells = smells.refactored;
        this.deletedSmells = smells.deleted;
    }

    public int compareTo(CommitSmell commitSmell) {
        return Integer.valueOf(this.commitNumber)
                .compareTo(commitSmell.commitNumber);
    }
}
