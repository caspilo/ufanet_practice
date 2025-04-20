package org.example.core.task;

import java.util.Map;

public class PushNotification implements Schedulable {

    @Override
    public boolean execute(Map<String, String> params) {
        System.out.println("Push for User with ID " + params.get("ID") + ": " + params.get("message"));
        return true;
    }
}
