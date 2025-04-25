package org.example.core.logging.handler;

import org.example.core.logging.LogService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;

public class DailyRotatingFileHandler extends FileHandler {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private volatile String currentDate;
    private long lastCheckTime;


    public DailyRotatingFileHandler(String pattern, long limit, int count, boolean append) throws IOException {
        super(generateDailyRotatingFileName(pattern), limit, count, append);
        this.currentDate = dateFormat.format(new Date());
    }

    @Override
    public synchronized void publish(LogRecord record) {
        String newDate = getCurrentDate();
        if (!currentDate.equals(newDate)) {
            try {
                this.close();
                super.publish(record);
                this.currentDate = newDate;
            } catch (Exception e) {
                LogService.logger.warning("Ошибка при переключении файла логов. " + e.getMessage());
            }
        }
        super.publish(record);
    }

    public String getCurrentDate() {
        long now = System.currentTimeMillis();
        if (now - lastCheckTime > 1000) {
            synchronized (this) {
                if (now - lastCheckTime > 1000) {
                    currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                    lastCheckTime = now;
                }
            }
        }
        return currentDate;
    }

    private static String generateDailyRotatingFileName(String pattern) {

        return pattern.replace("%d", dateFormat.format(new Date()));
    }
}
