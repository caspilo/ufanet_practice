package org.example.core.validator;

import org.example.core.schedulable.Schedulable;

import java.lang.reflect.InvocationTargetException;

public class ScheduleClassValidator {

    public static boolean validateTaskClass(Class clazz) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String scheduleClassName = clazz.getName();
        if (!(Class.forName(scheduleClassName).getDeclaredConstructor().newInstance() instanceof Schedulable)) {
            throw new RuntimeException("Class with name :" + scheduleClassName + " does not implements interface with name: " + Schedulable.class.getName());
        }
        return true;
    }
}
