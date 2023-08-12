package dev.isxander.yacl3.config.v2.api;

import java.lang.reflect.Type;

public interface FieldAccess<T> {
    T get();

    void set(T value);

    String name();

    Type type();

}
