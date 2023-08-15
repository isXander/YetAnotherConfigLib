package dev.isxander.yacl3.config.v2.api;

import java.lang.reflect.Type;

public interface ReadOnlyFieldAccess<T> {
    T get();

    String name();

    Type type();

    Class<T> typeClass();
}
