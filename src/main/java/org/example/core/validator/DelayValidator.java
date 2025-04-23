package org.example.core.validator;

import org.example.core.entity.DelayParams;
import org.example.core.logging.LogService;
import org.example.core.service.task.scheduler.Delay;

public class DelayValidator {

    public static boolean validateParams(Delay params) {

        if (params == null) {
            LogService.logger.severe("ERROR. Params is null");
            return false;
        }

        return validateParams(params.isWithRetry(), params.isFixedRetryPolicy(), params.getDelayBase(), params.getFixDelayValue(), params.getMaxRetryCount(), params.getDelayLimit());
    }

    private static boolean validateParams(boolean withRetry, boolean fixedRetryPolicy, Long delayBase, Long fixDelayValue, int maxRetryCount, Long delayLimit) {

        if (withRetry) {

            if (fixedRetryPolicy) {
                if (fixDelayValue < 0) {
                    LogService.logger.severe("ERROR. Incorrect value for parameter fixDelayValue = " + fixDelayValue + ". Value can`t be < 0");
                    return false;
                }
            } else {
                if (delayBase < 0) {
                    LogService.logger.severe("ERROR. Incorrect value for parameter delayBase = " + delayBase + ". Value can`t be < 0");
                    return false;
                }
            }
            if (maxRetryCount < 0) {
                LogService.logger.severe("ERROR. Incorrect value for parameter maxRetryCount = " + maxRetryCount + ". Value can`t be < 0");
                return false;
            }
            if (delayLimit < 0) {
                LogService.logger.severe("ERROR. Incorrect value for parameter delayLimit = " + delayLimit + ". Value can`t be < 0");
                return false;
            }
        }

        return true;
    }
}
