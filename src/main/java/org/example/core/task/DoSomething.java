package org.example.core.task;

import java.util.Map;

public class DoSomething implements Schedulable {

    @Override
    public void execute(Map<String,String> params) {
        System.out.println("Message for User with ID " + params.get("ID") + ": " + params.get("message"));
    }
}
