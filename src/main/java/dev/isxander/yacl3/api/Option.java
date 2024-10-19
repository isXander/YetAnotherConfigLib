package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.impl.OptionImpl;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Option<T> {
    /**
     * Name of the option
     */
    @NotNull Component name();

    @NotNull OptionDescription description();

    /**
     * Tooltip (or description) of the option.
     * Rendered on hover.
     */
    @Deprecated
    @NotNull Component tooltip();

    /**
     * Widget provider for a type of option.
     *
     * @see dev.isxander.yacl3.gui.controllers
     */
    @NotNull Controller<T> controller();

    @NotNull StateManager<T> stateManager();

    /**
     * Binding for the option.
     * Controls setting, getting and default value.
     *
     * @see Binding
     */
    @Deprecated
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
    void requestSet(@NotNull T value);

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

    void addEventListener(OptionEventListener<T> listener);

    /**
     * Adds a listener for when the pending value changes
     */
    @Deprecated
    void addListener(BiConsumer<Option<T>, T> changedListener);

    static <T> Builder<T> createBuilder() {
        return new OptionImpl.BuilderImpl<>();
    }

    /**
     * Creates a builder to construct an {@link Option}
     *
     * @param <T> type of the option's value
     * @param typeClass used to capture the type
     */
    @Deprecated
    static <T> Builder<T> createBuilder(Class<T> typeClass) {
        return createBuilder();
    }

    interface Builder<T> {
        /**
         * Sets the name to be used by the option.
         *
         * @see Option#name()
         */
        Builder<T> name(@NotNull Component name);

        /**
         * Sets the description to be used by the option.
         * @see OptionDescription
         * @param description the static description.
         * @return this builder
         */
        Builder<T> description(@NotNull OptionDescription description);

        /**
         * Sets the function to get the description by the option's current value.
         *
         * @see OptionDescription
         * @param descriptionFunction the function to get the description by the option's current value.
         * @return this builder
         */
        Builder<T> description(@NotNull Function<T, OptionDescription> descriptionFunction);

        /**
         * Supplies a controller for this option. A controller is the GUI control to interact with the option.
         * @return this builder
         */
        Builder<T> controller(@NotNull Function<Option<T>, ControllerBuilder<T>> controllerBuilder);

        /**
         * Sets the controller for the option.
         * This is how you interact and change the options.
         *
         * @see dev.isxander.yacl3.gui.controllers
         */
        Builder<T> customController(@NotNull Function<Option<T>, Controller<T>> control);

        Builder<T> stateManager(@NotNull StateManager<T> stateManager);

        /**
         * Sets the binding for the option.
         * Used for default, getter and setter.
         * Under-the-hood, this creates a state manager that is individual to the option, sharing state with no options.
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
        Builder<T> flags(@NotNull Collection<? extends OptionFlag> flags);

        Builder<T> addListener(@NotNull OptionEventListener<T> listener);

        Builder<T> addListeners(@NotNull Collection<OptionEventListener<T>> listeners);

        /**
         * Instantly invokes the binder's setter when modified in the GUI.
         * Prevents the user from undoing the change
         * <p>
         * Does not support {@link Option#flags()}!
         */
        @Deprecated
        Builder<T> instant(boolean instant);

        /**
         * Adds a listener to the option. Invoked upon changing the pending value.
         *
         * @see Option#addListener(BiConsumer)
         */
        @Deprecated
        Builder<T> listener(@NotNull BiConsumer<Option<T>, T> listener);

        /**
         * Adds multiple listeners to the option. Invoked upon changing the pending value.
         *
         * @see Option#addListener(BiConsumer)
         */
        @Deprecated
        Builder<T> listeners(@NotNull Collection<BiConsumer<Option<T>, T>> listeners);

        Option<T> build();
    }
}
