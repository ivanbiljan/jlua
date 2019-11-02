package com.jlua.helpers;

import com.jlua.internal.Func1;
import com.jlua.internal.Predicate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.util.*;

public final class CollectionHelper {
    public static <T> Collection<T> cast(@NotNull Collection collection) {
        assert collection != null : "collection must not be null";

        Set<T> output = new HashSet<>();
        for (Object element : collection) {
            output.add((T) element);
        }

        return output;
    }

    public static <T> Collection<T> concat(@NotNull Collection<T>... collections) {
        assert collections != null : "collections must not be null";

        return new HashSet<T>() {{
            for (Collection<T> collection : collections) {
                addAll(collection);
            }
        }};
    }

    public static <T> Collection<T> filter(@NotNull Collection<T> collection, @NotNull Func1<T, Boolean> predicate) {
        assert collection != null : "set must not be null";
        assert predicate != null : "predicate must not be null";

        HashSet<T> output = new HashSet<T>();
        for (T element : collection) {
            if (predicate.Invoke(element, null, null, null, null)) {
                output.add(element);
            }
        }

        return output;
    }

    public static <T> T first(Collection<T> collection, Func1<T, Boolean> predicate) {
        assert collection != null : "collection must not be null";
        assert predicate != null : "predicate must not be null";

        for (T element : collection) {
            if (predicate.Invoke(element, null, null, null, null)) {
                return element;
            }
        }

        return null;
    }
}
