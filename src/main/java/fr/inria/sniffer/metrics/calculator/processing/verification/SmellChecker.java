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
package fr.inria.sniffer.metrics.calculator.processing.verification;

import fr.inria.sniffer.metrics.calculator.SmellCode;

/**
 * Performs some checks on smells.
 */
public interface SmellChecker {
    /**
     * Verify if the smell has been refactored or simply removed from the source code.
     *
     * @param code       The smell code name.
     * @param instance   The smell instance definition.
     * @param commitHash The commit in which this smell has supposedly been refactored.
     * @return true if the smell has disappeared after an actual refactoring,
     * false if the source containing this smells has simply been wiped.
     */
    boolean isActualRefactoring(SmellCode code, String instance, String commitHash);
}
