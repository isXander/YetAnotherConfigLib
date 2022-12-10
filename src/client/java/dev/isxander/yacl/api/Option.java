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
        return new Builder<>(typeClass);
    }

    class Builder<T> {
        private Text name = Text.literal("Name not specified!").formatted(Formatting.RED);

        private final List<Function<T, Text>> tooltipGetters = new ArrayList<>();

        private Function<Option<T>, Controller<T>> controlGetter;

        private Binding<T> binding;

        private boolean available = true;

        private boolean instant = false;

        private final Set<OptionFlag> flags = new HashSet<>();

        private final Class<T> typeClass;

        private final List<BiConsumer<Option<T>, T>> listeners = new ArrayList<>();

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
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltipGetter function to get tooltip depending on value {@link Builder#build()}.
         */
        @SafeVarargs
        public final Builder<T> tooltip(@NotNull Function<T, Text>... tooltipGetter) {
            Validate.notNull(tooltipGetter, "`tooltipGetter` cannot be null");

            this.tooltipGetters.addAll(List.of(tooltipGetter));
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
            Validate.notNull(tooltips, "`tooltips` cannot be empty");

            this.tooltipGetters.addAll(Stream.of(tooltips).map(text -> (Function<T, Text>) t -> text).toList());
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

        /**
         * Sets if the option can be configured
         *
         * @see Option#available()
         */
        public Builder<T> available(boolean available) {
            this.available = available;
            return this;
        }

        /**
         * Adds a flag to the option.
         * Upon applying changes, all flags are executed.
         * {@link Option#flags()}
         */
        public Builder<T> flag(@NotNull OptionFlag... flag) {
            Validate.notNull(flag, "`flag` must not be null");

            this.flags.addAll(Arrays.asList(flag));
            return this;
        }

        /**
         * Adds a flag to the option.
         * Upon applying changes, all flags are executed.
         * {@link Option#flags()}
         */
        public Builder<T> flags(@NotNull Collection<OptionFlag> flags) {
            Validate.notNull(flags, "`flags` must not be null");

            this.flags.addAll(flags);
            return this;
        }

        /**
         * Instantly invokes the binder's setter when modified in the GUI.
         * Prevents the user from undoing the change
         * <p>
         * Does not support {@link Option#flags()}!
         */
        public Builder<T> instant(boolean instant) {
            this.instant = instant;
            return this;
        }

        /**
         * Adds a listener to the option. Invoked upon changing the pending value.
         *
         * @see Option#addListener(BiConsumer)
         */
        public Builder<T> listener(@NotNull BiConsumer<Option<T>, T> listener) {
            this.listeners.add(listener);
            return this;
        }

        /**
         * Adds multiple listeners to the option. Invoked upon changing the pending value.
         *
         * @see Option#addListener(BiConsumer)
         */
        public Builder<T> listeners(@NotNull Collection<BiConsumer<Option<T>, T>> listeners) {
            this.listeners.addAll(listeners);
            return this;
        }

        /**
         * Dictates whether the option should require a restart.
         * {@link Option#requiresRestart()}
         */
        @Deprecated
        public Builder<T> requiresRestart(boolean requiresRestart) {
            if (requiresRestart) flag(OptionFlag.GAME_RESTART);
            else flags.remove(OptionFlag.GAME_RESTART);

            return this;
        }

        public Option<T> build() {
            Validate.notNull(controlGetter, "`control` must not be null when building `Option`");
            Validate.notNull(binding, "`binding` must not be null when building `Option`");
            Validate.isTrue(!instant || flags.isEmpty(), "instant application does not support option flags");

            Function<T, Text> concatenatedTooltipGetter = value -> {
                MutableText concatenatedTooltip = Text.empty();
                boolean first = true;
                for (Function<T, Text> line : tooltipGetters) {
                    if (!first) concatenatedTooltip.append("\n");
                    first = false;

                    concatenatedTooltip.append(line.apply(value));
                }

                return concatenatedTooltip;
            };

            if (instant) {
                listeners.add((opt, pendingValue) -> opt.applyValue());
            }

            return new OptionImpl<>(name, concatenatedTooltipGetter, controlGetter, binding, available, ImmutableSet.copyOf(flags), typeClass, listeners);
        }
    }
}
