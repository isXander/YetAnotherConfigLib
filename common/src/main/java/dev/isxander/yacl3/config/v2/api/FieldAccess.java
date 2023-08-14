package dev.isxander.yacl3.config.v2.api;

public interface FieldAccess<T> extends ReadOnlyFieldAccess<T> {
    void set(T value);
}
