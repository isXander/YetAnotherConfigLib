package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.GenericBindingImpl;
import dev.isxander.yacl.impl.MinecraftBindingImpl;
import dev.isxander.yacl.mixin.SimpleOptionAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.apache.commons.lang3.Validate;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Controls modifying the bound option.
 * Provides the default value, a setter and a getter.
 */
public interface Binding<T, S> {
    void setValue(S storage, T value);

    T getValue(S storage);

    T defaultValue(S storage);

    /**
     * Creates a generic binding.
     *
     * @param def default value of the option, used to reset
     * @param getter should return the current value of the option
     * @param setter should set the option to the supplied value
     */
    static <T, S> Binding<T, S> generic(Function<S, T> def, Function<S, T> getter, BiConsumer<S, T> setter) {
        Validate.notNull(def, "`def` must not be null");
        Validate.notNull(getter, "`getter` must not be null");
        Validate.notNull(setter, "`setter` must not be null");

        return new GenericBindingImpl<>(def, getter, setter);
    }

    /**
     * Creates a generic binding without consuming a {@link Storage}.
     *
     * @param def default value of the option, used to reset
     * @param getter should return the current value of the option
     * @param setter should set the option to the supplied value
     */
    static <T, S> Binding<T, S> generic(T def, Supplier<T> getter, Consumer<T> setter) {
        Validate.notNull(def, "`def` must not be null");
        Validate.notNull(getter, "`getter` must not be null");
        Validate.notNull(setter, "`setter` must not be null");

        return new GenericBindingImpl<>(storage -> def, storage -> getter.get(), (storage, value) -> setter.accept(value));
    }

    /**
     * Creates a {@link Binding} for Minecraft's {@link SimpleOption}
     */
    static <T> Binding<T, GameOptions> minecraft(Function<GameOptions, SimpleOption<T>> minecraftOption) {
        Validate.notNull(minecraftOption, "`minecraftOption` must not be null");

        return new MinecraftBindingImpl<>(minecraftOption);
    }

    /**
     * Creates an immutable binding that has no default and cannot be modified.
     *
     * @param value the value for the binding
     */
    static <T, S> Binding<T, S> immutable(T value) {
        Validate.notNull(value, "`value` must not be null");

        return generic(value, () -> value, newValue -> {});
    }
}
