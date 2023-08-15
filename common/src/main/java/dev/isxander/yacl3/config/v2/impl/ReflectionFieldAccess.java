package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.config.v2.api.FieldAccess;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public record ReflectionFieldAccess<T>(Field field, Object instance) implements FieldAccess<T> {
    @Override
    public T get() {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(T value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name() {
        return field.getName();
    }

    @Override
    public Type type() {
        return field.getGenericType();
    }

    @Override
    public Class<T> typeClass() {
        return (Class<T>) field.getType();
    }
}
