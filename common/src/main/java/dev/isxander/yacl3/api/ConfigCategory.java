package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.impl.ConfigCategoryImpl;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Separates {@link Option}s or {@link OptionGroup}s into multiple distinct sections.
 * Served to a user as a button in the left column,
 * upon pressing, the options list is filled with options contained within this category.
 */
public interface ConfigCategory {
    /**
     * Name of category, displayed as a button on the left column.
     */
    @NotNull Component name();

    /**
     * Gets every {@link OptionGroup} in this category.
     */
    @NotNull ImmutableList<OptionGroup> groups();

    /**
     * Tooltip (or description) of the category.
     * Rendered on hover.
     */
    @NotNull Component tooltip();

    /**
     * Creates a builder to construct a {@link ConfigCategory}
     */
    static Builder createBuilder() {
        return new ConfigCategoryImpl.BuilderImpl();
    }

    interface Builder extends OptionAddable {
        /**
         * Sets name of the category
         *
         * @see ConfigCategory#name()
         */
        Builder name(@NotNull Component name);

        /**
         * Adds an option to the root group of the category.
         * To add to another group, use {@link Builder#group(OptionGroup)}.
         * To construct an option, use {@link Option#createBuilder()}
         *
         * @see ConfigCategory#groups()
         * @see OptionGroup#isRoot()
         */
        @Override
        Builder option(@NotNull Option<?> option);

        /**
         * Adds an option to the root group of the category.
         * To add to another group, use {@link Builder#group(OptionGroup)}.
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
         * Adds an option to the root group of the category if a condition is met.
         * To add to another group, use {@link Builder#group(OptionGroup)}.
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
         * Adds an option to the root group of the category if a condition is met.
         * To add to another group, use {@link Builder#group(OptionGroup)}.
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
         * Adds multiple options to the root group of the category.
         * To add to another group, use {@link Builder#groups(Collection)}.
         * To construct an option, use {@link Option#createBuilder()}
         *
         * @see ConfigCategory#groups()
         * @see OptionGroup#isRoot()
         */
        @Override
        Builder options(@NotNull Collection<? extends Option<?>> options);

        /**
         * Adds an option group.
         * To add an option to the root group, use {@link Builder#option(Option)}
         * To construct a group, use {@link OptionGroup#createBuilder()}
         */
        Builder group(@NotNull OptionGroup group);

        /**
         * Adds multiple option groups.
         * To add multiple options to the root group, use {@link Builder#options(Collection)}
         * To construct a group, use {@link OptionGroup#createBuilder()}
         */
        Builder groups(@NotNull Collection<OptionGroup> groups);

        /**
         * Sets the tooltip to be used by the category.
         * Can be invoked twice to append more lines.
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltips text lines - merged with a new-line on {@link Builder#build()}.
         */
        Builder tooltip(@NotNull Component... tooltips);

        ConfigCategory build();
    }
}
