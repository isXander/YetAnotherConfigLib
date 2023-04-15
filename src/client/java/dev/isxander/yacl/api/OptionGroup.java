package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.impl.OptionGroupImpl;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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

    /**
     * Tooltip displayed on hover.
     */
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

        /**
         * Sets the tooltip to be used by the option group.
         * Can be invoked twice to append more lines.
         * No need to wrap the Component yourself, the gui does this itself.
         *
         * @param tooltips Component lines - merged with a new-line on {@link Builder#build()}.
         */
        Builder tooltip(@NotNull Component... tooltips);

        /**
         * Adds an option to group.
         * To construct an option, use {@link Option#createBuilder(Class)}
         *
         * @see OptionGroup#options()
         */
        @Override
        Builder option(@NotNull Option<?> option);

        /**
         * Adds multiple options to group.
         * To construct an option, use {@link Option#createBuilder(Class)}
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
