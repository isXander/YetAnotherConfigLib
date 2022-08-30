package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.GenericBindingImpl;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Binding<T> {
    void setValue(T value);

    T getValue();

    void resetValue();

    static <T> Binding<T> of(T def, Supplier<T> getter, Consumer<T> setter) {
        return new GenericBindingImpl<>(def, getter, setter);
    }
}
