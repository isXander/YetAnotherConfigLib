package dev.isxander.yacl3.api;

import dev.isxander.yacl3.impl.GenericBindingImpl;
import dev.isxander.yacl3.mixin.OptionInstanceAccessor;
import net.minecraft.client.OptionInstance;
import org.apache.commons.lang3.Validate;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Controls modifying the bound option.
 * Provides the default value, a setter and a getter.
 */
public interface Binding<T> {
    void setValue(T value);

    T getValue();

    T defaultValue();

    default <U> Binding<U> xmap(Function<T, U> to, Function<U, T> from) {
        return Binding.generic(to.apply(this.defaultValue()), () -> to.apply(this.getValue()), v -> this.setValue(from.apply(v)));
    }

    /**
     * Creates a generic binding.
     *
     * @param def default value of the option, used to reset
     * @param getter should return the current value of the option
     * @param setter should set the option to the supplied value
     */
    static <T> Binding<T> generic(T def, Supplier<T> getter, Consumer<T> setter) {
        Validate.notNull(def, "`def` must not be null");
        Validate.notNull(getter, "`getter` must not be null");
        Validate.notNull(setter, "`setter` must not be null");

        return new GenericBindingImpl<>(def, getter, setter);
    }

    /**
     * Creates a {@link Binding} for Minecraft's {@link OptionInstance}
     */
    static <T> Binding<T> minecraft(OptionInstance<T> minecraftOption) {
        Validate.notNull(minecraftOption, "`minecraftOption` must not be null");

        return new GenericBindingImpl<>(
                ((OptionInstanceAccessor<T>) (Object) minecraftOption).getInitialValue(),
                minecraftOption::get,
                minecraftOption::set
        );
    }

    /**
     * Creates an immutable binding that has no default and cannot be modified.
     *
     * @param value the value for the binding
     */
    static <T> Binding<T> immutable(T value) {
        Validate.notNull(value, "`value` must not be null");

        return new GenericBindingImpl<>(
                value,
                () -> value,
                changed -> {}
        );
    }
}
