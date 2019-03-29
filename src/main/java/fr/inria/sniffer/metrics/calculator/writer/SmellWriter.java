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
package fr.inria.sniffer.metrics.calculator.writer;

import fr.inria.sniffer.metrics.calculator.processing.CommitSmell;

import java.io.FileWriter;
import java.util.List;

/**
 * Common interface of a smell summary writer.
 */
public interface SmellWriter {

    /**
     * Handles the {@link FileWriter} opening and record the values.
     *
     * @param smells The analyzed smells to write out.
     */
    void write(List<CommitSmell> smells);
}
