package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.Binding;

public class SelfContainedBinding<T> implements Binding<T> {
    private T value;
    private final T defaultValue;

    public SelfContainedBinding(T value, T defaultValue) {
        this.value = value;
        this.defaultValue = defaultValue;
    }

    public SelfContainedBinding(T value) {
        this(value, value);
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public T defaultValue() {
        return this.defaultValue;
    }
}
