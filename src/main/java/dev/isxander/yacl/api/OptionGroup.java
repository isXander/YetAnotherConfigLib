package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.impl.OptionGroupImpl;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Serves as a separator between multiple chunks of options
 * that may be too similar or too few to be placed in a separate {@link ConfigCategory}.
 * Or maybe you just want your config to feel less dense.
 */
public interface OptionGroup {
    /**
     * Name of the option group, displayed as a separator in the option lists.
     * Can be empty.
     */
    Text name();

    /**
     * List of all options in the group
     */
    @NotNull ImmutableList<Option<?>> options();

    /**
     * Always false when using the {@link Builder}
     * used to not render the separator if true
     */
    boolean isRoot();

    /**
     * Creates a builder to construct a {@link OptionGroup}
     */
    static Builder createBuilder() {
        return new Builder();
    }

    class Builder {
        private Text name = Text.empty();
        private final List<Option<?>> options = new ArrayList<>();

        private Builder() {

        }

        /**
         * Sets name of the group, can be {@link Text#empty()} to just separate options, like sodium.
         *
         * @see OptionGroup#name()
         */
        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` must not be null");

            this.name = name;
            return this;
        }

        /**
         * Adds an option to group.
         * To construct an option, use {@link Option#createBuilder(Class)}
         *
         * @see OptionGroup#options()
         */
        public Builder option(@NotNull Option<?> option) {
            Validate.notNull(option, "`option` must not be null");

            this.options.add(option);
            return this;
        }

        /**
         * Adds multiple options to group.
         * To construct an option, use {@link Option#createBuilder(Class)}
         *
         * @see OptionGroup#options()
         */
        public Builder options(@NotNull Collection<Option<?>> options) {
            Validate.notEmpty(options, "`options` must not be empty");

            this.options.addAll(options);
            return this;
        }

        public OptionGroup build() {
            Validate.notEmpty(options, "`options` must not be empty to build `OptionGroup`");

            return new OptionGroupImpl(name, ImmutableList.copyOf(options), false);
        }
    }
}
