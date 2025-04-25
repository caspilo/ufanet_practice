package org.example.core.logging;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogService {
    public static Logger logger = null;
    static {
        try {
            logger = Logger.getLogger(LogService.class.getName());
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            FileHandler fh = new FileHandler("logs/log-" + today + ".txt", 1024 * 1024, 5, true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            //logger.setUseParentHandlers(false);
        } catch (Exception e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
    }
}
