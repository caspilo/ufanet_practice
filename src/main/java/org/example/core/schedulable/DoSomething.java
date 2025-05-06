package org.example.core.schedulable;

import java.util.Map;
import java.util.Random;

public class DoSomething implements Schedulable {

    private final Random random = new Random();

    @Override
    public boolean execute(Map<String,String> params) {

        int ex_time = random.nextInt(5000) + 1000;

        try {
            Thread.sleep(ex_time); // имитация процесса выполнения
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Message for User with ID " + params.get("ID") + ": " + params.get("message"));
        return true;
    }
}
