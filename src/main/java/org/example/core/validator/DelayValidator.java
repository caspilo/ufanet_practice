package org.example.core.validator;

import org.example.core.logging.LogService;
import org.example.core.service.task.scheduler.Delay;

public class DelayValidator {

    public static boolean validateParams(Delay params) {

        if (params == null) {
            LogService.logger.severe("ERROR. Delay params is null");
            return false;
        }

        return validateParams(params.isWithRetry(), params.getFixDelayValue(), params.getMaxRetryCount());
    }

    private static boolean validateParams(boolean withRetry, Long fixDelayValue, int maxRetryCount) {

        if (withRetry) {
            if (fixDelayValue <= 0) {
                LogService.logger.severe("ERROR. You are trying to schedule task with retry and fixed delay policy. " +
                        "Fix delay value should be greater than 0. You have set the value: " + fixDelayValue);
                return false;
            }
            if (maxRetryCount <= 0) {
                LogService.logger.severe("ERROR. You are trying to schedule task with retry. " +
                        "Max retry count should be greater than 0. You have set the value: " + maxRetryCount);
                return false;
            }
        }
        return true;
    }
}
