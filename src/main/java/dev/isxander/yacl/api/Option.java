package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.OptionImpl;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Option<T, S> {
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
    @NotNull Binding<T, S> binding();

    @NotNull Storage<S> storage();

    /**
     * Class of the option type.
     * Used by some controllers.
     */
    @NotNull Class<T> typeClass();

    /**
     * Checks if the pending value is not equal to the current set value
     */
    boolean changed();

    /**
     * If true, modifying this option recommends a restart.
     */
    boolean requiresRestart();

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
     * @return if there were changes to apply
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
     * Creates a builder to construct an {@link Option}
     *
     * @param <T> type of the option's value
     * @param typeClass used to capture the type
     */
    static <T, S> Builder<T, S> createBuilder(Class<T> typeClass, Storage<S> storage) {
        return new Builder<>(typeClass, storage);
    }

    static <T> Builder<T, ?> createBuilder(Class<T> typeClass) {
        return createBuilder(typeClass, Storage.EMPTY);
    }

    class Builder<T, S> {
        private Text name = Text.literal("Name not specified!").formatted(Formatting.RED);

        private final List<Text> tooltipLines = new ArrayList<>();

        private Function<Option<T, S>, Controller<T>> controlGetter;

        private Binding<T, S> binding;

        private final Storage<S> storage;

        private boolean requiresRestart;

        private final Class<T> typeClass;

        private Builder(Class<T> typeClass, Storage<S> storage) {
            this.typeClass = typeClass;
            this.storage = storage;
        }

        /**
         * Sets the name to be used by the option.
         *
         * @see Option#name()
         */
        public Builder<T, S> name(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        /**
         * Sets the tooltip to be used by the option.
         * Can be invoked twice to append more lines.
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltips text lines - merged with a new-line on {@link Builder#build()}.
         */
        public Builder<T, S> tooltip(@NotNull Text... tooltips) {
            Validate.notNull(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        /**
         * Sets the controller for the option.
         * This is how you interact and change the options.
         *
         * @see dev.isxander.yacl.gui.controllers
         */
        public Builder<T, S> controller(@NotNull Function<Option<T, S>, Controller<T>> control) {
            Validate.notNull(control, "`control` cannot be null");

            this.controlGetter = control;
            return this;
        }

        /**
         * Sets the binding for the option.
         * Used for default, getter and setter.
         *
         * @see Binding
         */
        public Builder<T, S> binding(@NotNull Binding<T, S> binding) {
            Validate.notNull(binding, "`binding` cannot be null");

            this.binding = binding;
            return this;
        }

        /**
         * Sets the binding for the option.
         * Shorthand of {@link Binding#generic(Object, Supplier, Consumer)}
         *
         * @param def default value of the option, used to reset
         * @param getter should return the current value of the option
         * @param setter should set the option to the supplied value
         * @see Binding
         */
        public Builder<T, S> binding(@NotNull Function<S, T> def, @NotNull Function<S, T> getter, @NotNull BiConsumer<S, T> setter) {
            Validate.notNull(def, "`def` must not be null");
            Validate.notNull(getter, "`getter` must not be null");
            Validate.notNull(setter, "`setter` must not be null");

            this.binding = Binding.generic(def, getter, setter);
            return this;
        }

        /**
         * Sets the binding for the option.
         * Shorthand of {@link Binding#generic(Object, Supplier, Consumer)}
         *
         * @param def default value of the option, used to reset
         * @param getter should return the current value of the option
         * @param setter should set the option to the supplied value
         * @see Binding
         */
        public Builder<T, S> binding(@NotNull T def, @NotNull Supplier<@NotNull T> getter, @NotNull Consumer<@NotNull T> setter) {
            Validate.notNull(def, "`def` must not be null");
            Validate.notNull(getter, "`getter` must not be null");
            Validate.notNull(setter, "`setter` must not be null");

            this.binding = Binding.generic(def, getter, setter);
            return this;
        }

        /**
         * Dictates whether the option should require a restart.
         * {@link Option#requiresRestart()}
         */
        public Builder<T, S> requiresRestart(boolean requiresRestart) {
            this.requiresRestart = requiresRestart;
            return this;
        }

        public Option<T, S> build() {
            Validate.notNull(controlGetter, "`control` must not be null when building `Option`");
            Validate.notNull(binding, "`binding` must not be null when building `Option`");

            MutableText concatenatedTooltip = Text.empty();
            boolean first = true;
            for (Text line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new OptionImpl<>(name, concatenatedTooltip, controlGetter, binding, storage, requiresRestart, typeClass);
        }
    }
}
