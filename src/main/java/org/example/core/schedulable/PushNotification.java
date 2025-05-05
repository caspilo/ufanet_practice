package org.example.core.schedulable;

import java.util.Map;

public class PushNotification implements Schedulable {

    @Override
    public boolean execute(Map<String, String> params) {
        if (params.containsKey("ID")) {
            try {
                Thread.sleep(2000); // имитация процесса выполнения
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Push for User with ID " + params.get("ID") + ": " + params.get("message"));
            return true;
        }
        return false;
    }
}
