package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Binding;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class GenericBindingImpl<T> implements Binding<T> {
    private final T def;
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    public GenericBindingImpl(T def, Supplier<T> getter, Consumer<T> setting) {
        this.def = def;
        this.getter = getter;
        this.setter = setting;
    }


    @Override
    public void setValue(T value) {
        setter.accept(value);
    }

    @Override
    public T getValue() {
        return getter.get();
    }

    @Override
    public T defaultValue() {
        return def;
    }

}
