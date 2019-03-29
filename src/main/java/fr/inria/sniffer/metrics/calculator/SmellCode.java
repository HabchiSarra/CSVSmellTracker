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
package fr.inria.sniffer.metrics.calculator;

/**
 * Describe available smells
 * and their offset in the output CSV file.
 */
public enum SmellCode {
    HMU(0),
    IOD(1),
    IWR(2),
    LIC(3),
    MIM(4),
    NLMR(5),
    UCS(6),
    UHA(7),
    UIO(8);

    public final int offset;

    SmellCode(int offset) {
        this.offset = offset;
    }
}
