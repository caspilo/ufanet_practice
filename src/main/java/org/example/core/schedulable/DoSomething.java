package org.example.core.schedulable;

import java.util.Map;

public class DoSomething implements Schedulable {

    @Override
    public boolean execute(Map<String,String> params) {
        try {
            Thread.sleep(1000); // имитация процесса выполнения
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Message for User with ID " + params.get("ID") + ": " + params.get("message"));
        return true;
    }
}
