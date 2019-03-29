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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse a commit input from the output of GitMiner.
 *
 * We are expecting an input of the form:
 * hash, login, id, mail
 */
public class InputCommit {
    private static final Logger logger = LoggerFactory.getLogger(InputCommit.class.getName());

    private static final String SEPARATOR = ",";

    public final String hash;
    public final String userName;
    public final String userEmail;
    public final String userId;

    private InputCommit(String hash, String userName, String userEmail, String userId) {
        this.hash = hash;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
    }

    public static InputCommit fromInputLine(String line) {
        String[] content = line.split(SEPARATOR);
        if (content.length < 4) {
            logger.warn("Unable to parse commit input: " + line);
        }
        return new InputCommit(content[0], content[1], content[3], content[2]);
    }
}
