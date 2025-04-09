package org.example.test;

import java.util.Map;

public class DoSomething implements Schedualable {

    @Override
    public void execute(Map<String, String> param) {
        System.out.println("Message for User with ID " + param.get("userID") + ": " + param.get("message"));
    }
}
