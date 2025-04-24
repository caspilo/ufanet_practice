package org.example.core.validator;

import org.example.core.entity.DelayParams;
import org.example.core.logging.LogService;
import org.example.core.service.task.scheduler.Delay;

public class DelayValidator {

    public static boolean validateParams(Delay params) {

        if (params == null) {
            LogService.logger.severe("ERROR. Delay params is null");
            return false;
        }

        return validateParams(params.isWithRetry(), params.isFixedRetryPolicy(), params.getDelayBase(), params.getFixDelayValue(), params.getMaxRetryCount(), params.getDelayLimit());
    }

    private static boolean validateParams(boolean withRetry, boolean fixedRetryPolicy, Long delayBase, Long fixDelayValue, int maxRetryCount, Long delayLimit) {

        if (withRetry) {
            if (fixedRetryPolicy) {
                if (fixDelayValue <= 0) {
                    LogService.logger.severe("ERROR. You are trying to schedule task with retry and fixed delay policy. " +
                            "Fix delay value should be greater than 0. You have set the value: " + fixDelayValue);
                    return false;
                }
            } else {
                if (delayBase <= 0) {
                    LogService.logger.severe("ERROR. You are trying to schedule task with retry and function delay policy. " +
                            "Delay base should be greater than 0. You have set the value: " + delayBase);
                    return false;
                }

                if (delayLimit <= 0) {
                    LogService.logger.severe("ERROR. You are trying to schedule task with retry. " +
                            "Delay limit should be greater than 0. You have set the value: " + delayLimit);
                    return false;
                }
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
