package org.example.core.logging;

import org.example.core.logging.handler.DailyRotatingFileHandler;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogService {
    public static Logger logger = null;
    static {
        try {
            logger = Logger.getLogger(LogService.class.getName());
            FileHandler fh = new DailyRotatingFileHandler("logs/app-%d.log", 1024 * 1024, 5, true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
    }
}