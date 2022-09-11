package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.EmptyStorage;

public interface Storage<T> {
    EmptyStorage EMPTY = new EmptyStorage();

    T data();

    void save();
    void load();
}
