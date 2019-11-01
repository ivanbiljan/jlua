package com.jlua.helpers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class ClassMetadata {
    private Set<Field> instanceFields;
    private Set<Field> staticFields;
    private Set<Constructor> constructors;
    private Set<Method> instanceMethods;
    private Set<Method> staticMethods;

    public ClassMetadata(@NotNull Class clazz) {
        assert clazz != null : "clazz must not be null";

        HashSet<Method> methods = new HashSet<>();
        for (Method method : clazz.getMethods()) {
            String name = method.getName();
            if (name == "equals" || name == "hashCode" || name == "toString" || name == "getClass") {
                continue;
            }

            methods.add(method);
        }

        constructors = (Set<Constructor>) Arrays.asList(clazz.getConstructors());
        instanceFields = (Set<Field>) CollectionHelper.filter(Arrays.asList(clazz.getFields()),
                (field, aVoid, aVoid2, aVoid3, aVoid4) -> !Modifier.isStatic(field.getModifiers()));
        staticFields = (Set<Field>) CollectionHelper.filter(Arrays.asList(clazz.getFields()),
                (field, aVoid, aVoid2, aVoid3, aVoid4) -> Modifier.isStatic(field.getModifiers()));
        instanceMethods = (Set<Method>) CollectionHelper.filter(methods,
                (method, aVoid, aVoid2, aVoid3, avoid4) -> Modifier.isStatic(method.getModifiers()));
        staticMethods = (Set<Method>) CollectionHelper.filter(methods,
                (method, aVoid, aVoid2, aVoid3, avoid4) -> !Modifier.isStatic(method.getModifiers()));
    }

    @NotNull
    public ClassMetadata fromObject(Object object) {
        return new ClassMetadata(object.getClass());
    }

    @Contract(pure = true)
    public Set<Constructor> getConstructors() {
        return constructors;
    }

    @NotNull
    public Collection<Member> getInstanceMembers() {
        return CollectionHelper.concat(CollectionHelper.cast(instanceFields),
                CollectionHelper.cast(instanceMethods));
    }

    @NotNull
    public Collection<Member> getStaticMembers() {
        return CollectionHelper.concat(CollectionHelper.cast(staticFields), CollectionHelper.cast(staticMethods));
    }

    // This is a helper method I created solely for the purpose of not having to deal with this in C
    public Member getMemberByName(String name, Boolean isStatic) {
        if (isStatic) {
            return CollectionHelper.first(getStaticMembers(),
                    (obj, aVoid, aVoid2, aVoid3, aVoid4) -> obj.getName() == name);
        }

        return CollectionHelper.first(getInstanceMembers(),
                (obj, aVoid, aVoid2, aVoid3, aVoid4) -> obj.getName() == name);
    }
}
