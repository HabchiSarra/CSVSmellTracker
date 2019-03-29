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
package fr.inria.sniffer.metrics.calculator.devparser;

class CommitCounter {
    /**
     * Total number of commits the in the project.
     */
    private int totalCommits;

    /**
     * Number of analyzed commits the developer in the project.
     */
    private int analyzedCommits;

    void addCommit(boolean parsed) {
        totalCommits++;
        if (parsed) {
            analyzedCommits++;
        }
    }

    public int getAnalyzedCommits() {
        return analyzedCommits;
    }

    public int getTotalCommits() {
        return totalCommits;
    }
}
