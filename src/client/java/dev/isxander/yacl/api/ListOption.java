package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl.impl.ListOptionImpl;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ListOption<T> extends OptionGroup, Option<List<T>> {
    @Override
    @NotNull ImmutableList<ListOptionEntry<T>> options();

    /**
     * Class of the entry type
     */
    @NotNull Class<T> elementTypeClass();

    @ApiStatus.Internal
    ListOptionEntry<T> insertNewEntryToTop();

    @ApiStatus.Internal
    void insertEntry(int index, ListOptionEntry<?> entry);

    @ApiStatus.Internal
    int indexOf(ListOptionEntry<?> entry);

    @ApiStatus.Internal
    void removeEntry(ListOptionEntry<?> entry);

    @ApiStatus.Internal
    void addRefreshListener(BiConsumer<Option<List<T>>, List<T>> changedListener);

    static <T> Builder<T> createBuilder(Class<T> typeClass) {
        return new Builder<>(typeClass);
    }

    class Builder<T> {
        private Text name = Text.empty();
        private final List<Text> tooltipLines = new ArrayList<>();
        private Function<ListOptionEntry<T>, Controller<T>> controllerFunction;
        private Binding<List<T>> binding = null;
        private final Set<OptionFlag> flags = new HashSet<>();
        private T initialValue;
        private boolean collapsed = false;
        private boolean available = true;
        private final Class<T> typeClass;

        private Builder(Class<T> typeClass) {
            this.typeClass = typeClass;
        }

        /**
         * Sets name of the group, can be {@link Text#empty()} to just separate options, like sodium.
         *
         * @see ListOption#name()
         */
        public Builder<T> name(@NotNull Text name) {
            Validate.notNull(name, "`name` must not be null");

            this.name = name;
            return this;
        }

        /**
         * Sets the tooltip to be used by the option group.
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
         * Sets the value that is used when creating new entries
         */
        public Builder<T> initial(@NotNull T initialValue) {
            Validate.notNull(initialValue, "`initialValue` cannot be empty");

            this.initialValue = initialValue;
            return this;
        }

        /**
         * Sets the controller for the option.
         * This is how you interact and change the options.
         *
         * @see dev.isxander.yacl.gui.controllers
         */
        public Builder<T> controller(@NotNull Function<ListOptionEntry<T>, Controller<T>> control) {
            Validate.notNull(control, "`control` cannot be null");

            this.controllerFunction = control;
            return this;
        }

        /**
         * Sets the binding for the option.
         * Used for default, getter and setter.
         *
         * @see Binding
         */
        public Builder<T> binding(@NotNull Binding<List<T>> binding) {
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
        public Builder<T> binding(@NotNull List<T> def, @NotNull Supplier<@NotNull List<T>> getter, @NotNull Consumer<@NotNull List<T>> setter) {
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
         * Dictates if the group should be collapsed by default
         *
         * @see OptionGroup#collapsed()
         */
        public Builder<T> collapsed(boolean collapsible) {
            this.collapsed = collapsible;
            return this;
        }

        public ListOption<T> build() {
            Validate.notNull(initialValue, "`initialValue` must not be null");

            MutableText concatenatedTooltip = Text.empty();
            boolean first = true;
            for (Text line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new ListOptionImpl<>(name, concatenatedTooltip, binding, initialValue, typeClass, controllerFunction, ImmutableSet.copyOf(flags), collapsed, available);
        }
    }
}
