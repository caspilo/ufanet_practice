package org.example.core.schedulable;

import java.util.Map;

public class DoSomething implements Schedulable {

    @Override
    public boolean execute(Map<String,String> params) {
        System.out.println("Message for User with ID " + params.get("ID") + ": " + params.get("message"));
        return true;
    }
}
