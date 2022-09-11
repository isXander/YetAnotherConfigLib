package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.mixin.SimpleOptionAccessor;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

import java.util.function.Function;

public class MinecraftBindingImpl<T> implements Binding<T, GameOptions> {
    private final Function<GameOptions, SimpleOption<T>> option;

    public MinecraftBindingImpl(Function<GameOptions, SimpleOption<T>> option) {
        this.option = option;
    }

    @Override
    public void setValue(GameOptions storage, T value) {
        if (storage == null)
            return;

        option.apply(storage).setValue(value);
    }

    @Override
    public T getValue(GameOptions storage) {
        if (storage == null)
            return null;

        return option.apply(storage).getValue();
    }

    @Override
    public T defaultValue(GameOptions storage) {
        if (storage == null)
            return null;

        return ((SimpleOptionAccessor<T>) (Object) option).getDefaultValue();
    }
}
