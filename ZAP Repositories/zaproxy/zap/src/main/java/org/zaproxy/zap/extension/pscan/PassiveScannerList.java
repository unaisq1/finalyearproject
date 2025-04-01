/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2011 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.pscan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("removal")
@Deprecated(forRemoval = true, since = "2.16.0")
public class PassiveScannerList {

    private static final Logger LOGGER = LogManager.getLogger(PassiveScannerList.class);

    private List<PassiveScanner> passiveScanners = new CopyOnWriteArrayList<>();
    private Set<String> scannerNames = new HashSet<>();

    protected boolean add(PassiveScanner scanner) {
        if (scannerNames.contains(scanner.getName())) {
            LOGGER.warn(
                    "A scan rule with the name \"{}\" already exists. The rule \"{}\" will not be loaded.",
                    scanner.getName(),
                    scanner.getClass().getName());
            return false;
        }
        passiveScanners.add(scanner);
        scannerNames.add(scanner.getName());

        return true;
    }

    protected List<PassiveScanner> list() {
        return this.passiveScanners;
    }

    public void setAutoTagScanners(
            List<org.zaproxy.zap.extension.pscan.scanner.RegexAutoTagScanner> autoTagScanners) {
        List<PassiveScanner> tempScanners =
                new ArrayList<>(passiveScanners.size() + autoTagScanners.size());

        for (PassiveScanner scanner : passiveScanners) {
            if (scanner instanceof org.zaproxy.zap.extension.pscan.scanner.RegexAutoTagScanner) {
                this.scannerNames.remove(scanner.getName());
            } else {
                tempScanners.add(scanner);
            }
        }

        for (PassiveScanner scanner : autoTagScanners) {
            if (scannerNames.contains(scanner.getName())) {
                LOGGER.error("Duplicate passive scan rule name: {}", scanner.getName());
            } else {
                tempScanners.add(scanner);
                scannerNames.add(scanner.getName());
            }
        }

        this.passiveScanners = new CopyOnWriteArrayList<>(tempScanners);
    }

    public PassiveScanner removeScanner(String className) {
        for (PassiveScanner scanner : passiveScanners) {
            if (scanner.getClass().getName().equals(className)) {
                scannerNames.remove(scanner.getName());
                passiveScanners.remove(scanner);
                return scanner;
            }
        }
        return null;
    }

    /**
     * Returns the PassiveScan rule with the given id
     *
     * @param pluginId
     * @return the PassiveScan rule with the given id, or null if not found
     * @since 2.8.0
     */
    public PassiveScanner getScanner(int pluginId) {
        for (PassiveScanner scanner : passiveScanners) {
            if (scanner instanceof PluginPassiveScanner) {
                if (((PluginPassiveScanner) scanner).getPluginId() == pluginId) {
                    return scanner;
                }
            }
        }
        return null;
    }
}
