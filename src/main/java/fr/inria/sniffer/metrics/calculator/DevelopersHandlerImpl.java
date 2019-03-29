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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DevelopersHandlerImpl implements DevelopersHandler {
    private static final Logger logger = LoggerFactory.getLogger(DevelopersHandlerImpl.class.getName());

    private final Map<String, Integer> developersCode = new HashMap<>();

    private final Map<String, Set<String>> developersIntroduced = new HashMap<>();

    private final Map<String, Integer> developersSelfRefactored = new HashMap<>();

    private final Map<String, Integer> developerOtherRefactored = new HashMap<>();

    private final Map<String, Integer> developerSelfDeleted = new HashMap<>();

    private final Map<String, Integer> developerOtherDeleted = new HashMap<>();


    @Override
    public void notifyIntroduced(String developer, String smellId) {
        Set<String> introduced = developersIntroduced.getOrDefault(developer, new HashSet<>());
        introduced.add(smellId);
        developersIntroduced.put(developer, introduced);
    }

    @Override
    public void notifyRefactored(String developer, String smellId) {
        Set<String> introduced = developersIntroduced.get(developer);
        if (introduced != null && introduced.contains(smellId)) {
            addOneToCounter(developer, developersSelfRefactored);
        } else {
            addOneToCounter(developer, developerOtherRefactored);
        }
    }

    @Override
    public void notifyDeleted(String developer, String smellId) {
        Set<String> introduced = developersIntroduced.get(developer);
        if (introduced != null && introduced.contains(smellId)) {
            addOneToCounter(developer, developerSelfDeleted);
        } else {
            addOneToCounter(developer, developerOtherDeleted);
        }
    }

    private static void addOneToCounter(String developer, Map<String, Integer> refactorCounter) {
        Integer selfRefactored = refactorCounter.getOrDefault(developer, 0);
        selfRefactored += 1;
        refactorCounter.put(developer, selfRefactored);
    }

    @Override
    public long countIntroduced(String developer) {
        return developersIntroduced.getOrDefault(developer, new HashSet<>()).size();
    }

    @Override
    public long countRefactored(String developer) {
        return countSelfRefactored(developer) + countOtherRefactored(developer);
    }

    @Override
    public long countSelfRefactored(String developer) {
        return developersSelfRefactored.getOrDefault(developer, 0);
    }

    @Override
    public long countOtherRefactored(String developer) {
        return developerOtherRefactored.getOrDefault(developer, 0);
    }

    @Override
    public long countDeleted(String developer) {
        return countSelfDeleted(developer) + countOtherDeleted(developer);
    }

    @Override
    public long countSelfDeleted(String developer) {
        return developerSelfDeleted.getOrDefault(developer, 0);
    }

    @Override
    public long countOtherDeleted(String developer) {
        return developerOtherDeleted.getOrDefault(developer, 0);
    }

    @Override
    public void notify(String developer) {
        if (!developersCode.containsKey(developer)) {
            logger.trace("New developer notified: " + developer);
            developersCode.put(developer, developersCode.size() + 1);
        }
    }

    @Override
    public int size() {
        return developersCode.size();
    }

    @Override
    public int getOffset(String devName) {
        return developersCode.get(devName);
    }

    @Override
    public String[] sortedDevelopers() {
        String[] sorted = new String[developersCode.size()];
        for (String devId : developersCode.keySet()) {
            sorted[developersCode.get(devId) - 1] = devId;
        }
        return sorted;
    }
}
