package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl.impl.OptionImpl;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Option<T> {
    /**
     * Name of the option
     */
    @NotNull Text name();

    /**
     * Tooltip (or description) of the option.
     * Rendered on hover.
     */
    @NotNull Text tooltip();

    /**
     * Widget provider for a type of option.
     *
     * @see dev.isxander.yacl.gui.controllers
     */
    @NotNull Controller<T> controller();

    /**
     * Binding for the option.
     * Controls setting, getting and default value.
     *
     * @see Binding
     */
    @NotNull Binding<T> binding();

    /**
     * If the option can be configured
     */
    boolean available();

    /**
     * Sets if the option can be configured after being built
     *
     * @see Option#available()
     */
    void setAvailable(boolean available);

    /**
     * Class of the option type.
     * Used by some controllers.
     */
    @NotNull Class<T> typeClass();

    /**
     * Tasks that needs to be executed upon applying changes.
     */
    @NotNull ImmutableSet<OptionFlag> flags();

    /**
     * Checks if the pending value is not equal to the current set value
     */
    boolean changed();

    /**
     * Value in the GUI, ready to set the actual bound value or be undone.
     */
    @NotNull T pendingValue();

    /**
     * Sets the pending value
     */
    void requestSet(T value);

    /**
     * Applies the pending value to the bound value.
     * Cannot be undone.
     *
     * @return if there were changes to apply {@link Option#changed()}
     */
    boolean applyValue();

    /**
     * Sets the pending value to the bound value.
     */
    void forgetPendingValue();

    /**
     * Sets the pending value to the default bound value.
     */
    void requestSetDefault();

    /**
     * Checks if the current pending value is equal to its default value
     */
    boolean isPendingValueDefault();

    default boolean canResetToDefault() {
        return true;
    }

    /**
     * Adds a listener for when the pending value changes
     */
    void addListener(BiConsumer<Option<T>, T> changedListener);

    /**
     * Creates a builder to construct an {@link Option}
     *
     * @param <T> type of the option's value
     * @param typeClass used to capture the type
     */
    static <T> Builder<T> createBuilder(Class<T> typeClass) {
        return new OptionImpl.BuilderImpl<>(typeClass);
    }

    interface Builder<T> {
        /**
         * Sets the name to be used by the option.
         *
         * @see Option#name()
         */
        Builder<T> name(@NotNull Text name);

        /**
         * Sets the tooltip to be used by the option.
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltipGetter function to get tooltip depending on value {@link Builder#build()}.
         */
        @SuppressWarnings("unchecked")
        Builder<T> tooltip(@NotNull Function<T, Text>... tooltipGetter);

        /**
         * Sets the tooltip to be used by the option.
         * Can be invoked twice to append more lines.
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltips text lines - merged with a new-line on {@link Builder#build()}.
         */
        Builder<T> tooltip(@NotNull Text... tooltips);

        /**
         * Sets the controller for the option.
         * This is how you interact and change the options.
         *
         * @see dev.isxander.yacl.gui.controllers
         */
        Builder<T> controller(@NotNull Function<Option<T>, Controller<T>> control);

        /**
         * Sets the binding for the option.
         * Used for default, getter and setter.
         *
         * @see Binding
         */
        Builder<T> binding(@NotNull Binding<T> binding);

        /**
         * Sets the binding for the option.
         * Shorthand of {@link Binding#generic(Object, Supplier, Consumer)}
         *
         * @param def default value of the option, used to reset
         * @param getter should return the current value of the option
         * @param setter should set the option to the supplied value
         * @see Binding
         */
        Builder<T> binding(@NotNull T def, @NotNull Supplier<@NotNull T> getter, @NotNull Consumer<@NotNull T> setter);

        /**
         * Sets if the option can be configured
         *
         * @see Option#available()
         */
        Builder<T> available(boolean available);

        /**
         * Adds a flag to the option.
         * Upon applying changes, all flags are executed.
         * {@link Option#flags()}
         */
        Builder<T> flag(@NotNull OptionFlag... flag);

        /**
         * Adds a flag to the option.
         * Upon applying changes, all flags are executed.
         * {@link Option#flags()}
         */
        Builder<T> flags(@NotNull Collection<OptionFlag> flags);

        /**
         * Instantly invokes the binder's setter when modified in the GUI.
         * Prevents the user from undoing the change
         * <p>
         * Does not support {@link Option#flags()}!
         */
        Builder<T> instant(boolean instant);

        /**
         * Adds a listener to the option. Invoked upon changing the pending value.
         *
         * @see Option#addListener(BiConsumer)
         */
        Builder<T> listener(@NotNull BiConsumer<Option<T>, T> listener);

        /**
         * Adds multiple listeners to the option. Invoked upon changing the pending value.
         *
         * @see Option#addListener(BiConsumer)
         */
        Builder<T> listeners(@NotNull Collection<BiConsumer<Option<T>, T>> listeners);

        Option<T> build();
    }
}
