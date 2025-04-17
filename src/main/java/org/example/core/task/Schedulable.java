package org.example.core.task;

import java.util.Map;

public interface Schedulable {

    void execute(Map<String, String> params);

}
