package org.example.core.task;

import java.util.Map;

public interface Schedulable {

    boolean execute(Map<String, String> params);
}