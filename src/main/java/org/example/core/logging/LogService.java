package org.example.core.logging;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogService {
    public static Logger logger = null;

    static {
        try (FileInputStream ins = new FileInputStream("src/main/java/org/example/logging.properties")) {
            LogManager.getLogManager().readConfiguration(ins);
            logger = Logger.getLogger(LogService.class.getName());
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
    }
}
