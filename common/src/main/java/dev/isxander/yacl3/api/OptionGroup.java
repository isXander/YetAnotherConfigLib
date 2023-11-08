package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.impl.OptionGroupImpl;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

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
    Component name();

    OptionDescription description();

    /**
     * Tooltip displayed on hover.
     */
    @Deprecated
    Component tooltip();

    /**
     * List of all options in the group
     */
    @NotNull ImmutableList<? extends Option<?>> options();

    /**
     * Dictates if the group should be collapsed by default.
     */
    boolean collapsed();

    /**
     * Always false when using the {@link Builder}
     * used to not render the separator if true
     */
    boolean isRoot();

    /**
     * Creates a builder to construct a {@link OptionGroup}
     */
    static Builder createBuilder() {
        return new OptionGroupImpl.BuilderImpl();
    }

    interface Builder extends OptionAddable {
        /**
         * Sets name of the group, can be {@link Component#empty()} to just separate options, like sodium.
         *
         * @see OptionGroup#name()
         */
        Builder name(@NotNull Component name);

        Builder description(@NotNull OptionDescription description);

        /**
         * Adds an option to group.
         * To construct an option, use {@link Option#createBuilder(Class)}
         *
         * @see OptionGroup#options()
         */
        @Override
        Builder option(@NotNull Option<?> option);

        /**
         * Adds an option to this group.
         * To construct an option, use {@link Option#createBuilder()}
         *
         * @param optionSupplier to be called to initialise the option. called immediately
         * @return this
         */
        @Override
        default Builder option(@NotNull Supplier<@NotNull Option<?>> optionSupplier) {
            OptionAddable.super.option(optionSupplier);
            return this;
        }

        /**
         * Adds an option to this group if a condition is met.
         * To construct an option, use {@link Option#createBuilder()}
         *
         * @param condition only if true is the option added
         * @return this
         */
        @Override
        default Builder optionIf(boolean condition, @NotNull Option<?> option) {
            OptionAddable.super.optionIf(condition, option);
            return this;
        }

        /**
         * Adds an option to this group if a condition is met.
         * To construct an option, use {@link Option#createBuilder()}
         *
         * @param condition only if true is the option added
         * @param optionSupplier to be called to initialise the option. called immediately only if condition is true
         * @return this
         */
        @Override
        default Builder optionIf(boolean condition, @NotNull Supplier<@NotNull Option<?>> optionSupplier) {
            OptionAddable.super.optionIf(condition, optionSupplier);
            return this;
        }

        /**
         * Adds multiple options to group.
         * To construct an option, use {@link Option#createBuilder()}
         *
         * @see OptionGroup#options()
         */
        @Override
        Builder options(@NotNull Collection<? extends Option<?>> options);

        /**
         * Dictates if the group should be collapsed by default
         *
         * @see OptionGroup#collapsed()
         */
        Builder collapsed(boolean collapsible);

        OptionGroup build();
    }
}
