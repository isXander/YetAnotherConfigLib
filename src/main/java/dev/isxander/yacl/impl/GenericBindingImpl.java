package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Binding;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class GenericBindingImpl<T, S> implements Binding<T, S> {
    private final Function<S, T> def;
    private final Function<S, T> getter;
    private final BiConsumer<S, T> setter;

    public GenericBindingImpl(Function<S, T> def, Function<S, T> getter, BiConsumer<S, T> setting) {
        this.def = def;
        this.getter = getter;
        this.setter = setting;
    }


    @Override
    public void setValue(S storage, T value) {
        setter.accept(storage, value);
    }

    @Override
    public T getValue(S storage) {
        return getter.apply(storage);
    }

    @Override
    public T defaultValue(S storage) {
        return def.apply(storage);
    }

}
