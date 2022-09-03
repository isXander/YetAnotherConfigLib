package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.OptionImpl;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
     * Class of the option type.
     * Used by some controllers.
     */
    @NotNull Class<T> typeClass();

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
     */
    void applyValue();

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
    static <T> Builder<T> createBuilder(Class<T> typeClass) {
        return new Builder<>(typeClass);
    }

    class Builder<T> {
        private Text name;

        private final List<Text> tooltipLines = new ArrayList<>();

        private Function<Option<T>, Controller<T>> controlGetter;

        private Binding<T> binding;

        private final Class<T> typeClass;

        private Builder(Class<T> typeClass) {
            this.typeClass = typeClass;
        }

        /**
         * Sets the name to be used by the option.
         *
         * @see Option#name()
         */
        public Builder<T> name(@NotNull Text name) {
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
        public Builder<T> tooltip(@NotNull Text... tooltips) {
            Validate.notEmpty(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        /**
         * Sets the controller for the option.
         * This is how you interact and change the options.
         *
         * @see dev.isxander.yacl.gui.controllers
         */
        public Builder<T> controller(@NotNull Function<Option<T>, Controller<T>> control) {
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
        public Builder<T> binding(@NotNull Binding<T> binding) {
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
        public Builder<T> binding(@NotNull T def, @NotNull Supplier<@NotNull T> getter, @NotNull Consumer<@NotNull T> setter) {
            Validate.notNull(def, "`def` must not be null");
            Validate.notNull(getter, "`getter` must not be null");
            Validate.notNull(setter, "`setter` must not be null");

            this.binding = Binding.generic(def, getter, setter);
            return this;
        }

        public Option<T> build() {
            Validate.notNull(name, "`name` must not be null when building `Option`");
            Validate.notNull(controlGetter, "`control` must not be null when building `Option`");
            Validate.notNull(binding, "`binding` must not be null when building `Option`");

            MutableText concatenatedTooltip = Text.empty();
            boolean first = true;
            for (Text line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new OptionImpl<>(name, concatenatedTooltip, controlGetter, binding, typeClass);
        }
    }
}
