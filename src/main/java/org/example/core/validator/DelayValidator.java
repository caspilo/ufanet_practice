package org.example.core.validator;

import org.example.core.entity.DelayParams;

public class DelayValidator {

    public static boolean validateParams(DelayParams params) {

        if (params == null) {
            throw new IllegalArgumentException("Params are null");
        }

        return validateParams(params.isWithRetry(), params.isValueIsFixed(), params.getDelayBase(), params.getFixDelayValue(), params.getRetryCount(), params.getDelayLimit());
    }

    private static boolean validateParams(boolean withRetry, boolean fixedRetryPolicy, Long delayBase, Long fixDelayValue, int maxRetryCount, Long delayLimit) {

        if (withRetry) {

            if (fixedRetryPolicy) {
                if (fixDelayValue < 0) {
                    throw new RuntimeException("ERROR. Incorrect value for parameter fixDelayValue = " + fixDelayValue + ". Value can`t be < 0");
                }
            } else {
                if (delayBase < 0) {
                    throw new RuntimeException("ERROR. Incorrect value for parameter delayBase = " + delayBase + ". Value can`t be < 0");
                }
            }
            if (maxRetryCount < 0) {
                throw new RuntimeException("ERROR. Incorrect value for parameter maxRetryCount = " + maxRetryCount + ". Value can`t be < 0");
            }
            if (delayLimit < 0) {
                throw new RuntimeException("ERROR. Incorrect value for parameter delayLimit = " + delayLimit + ". Value can`t be < 0");
            }
        }

        return true;
    }
}
