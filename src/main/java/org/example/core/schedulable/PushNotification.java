package org.example.core.schedulable;

import java.util.Map;
import java.util.Random;

public class PushNotification implements Schedulable {

    private final Random random = new Random();

    @Override
    public boolean execute(Map<String, String> params) {

        int ex_time = random.nextInt(5000) + 1000;

        if (params.containsKey("ID")) {
            try {
                Thread.sleep(ex_time); // имитация процесса выполнения
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Push for User with ID " + params.get("ID") + ": " + params.get("message"));
            return true;
        }
        return false;
    }
}
