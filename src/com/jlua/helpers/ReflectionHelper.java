package com.jlua.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ReflectionHelper {
    public static Field getFieldSafe(Object object, String name) {
        for (Field field : object.getClass().getFields()) {
            if (!field.getName().equals(name)) {
                continue;
            }

            return field;
        }

        return null;
    }

    public static Method getMethodSafe(Object object, String name) {
        for (Method method : object.getClass().getMethods()) {
            if (!method.getName().equals(name)) {
                continue;
            }

            return method;
        }

        return null;
    }
}
