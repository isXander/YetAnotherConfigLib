package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.impl.ConfigCategoryImpl;
import dev.isxander.yacl.impl.OptionGroupImpl;
import dev.isxander.yacl.impl.utils.YACLConstants;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Separates {@link Option}s or {@link OptionGroup}s into multiple distinct sections.
 * Served to a user as a button in the left column,
 * upon pressing, the options list is filled with options contained within this category.
 */
public interface ConfigCategory {
    /**
     * Name of category, displayed as a button on the left column.
     */
    @NotNull Text name();

    /**
     * Gets every {@link OptionGroup} in this category.
     */
    @NotNull ImmutableList<OptionGroup> groups();

    /**
     * Tooltip (or description) of the category.
     * Rendered on hover.
     */
    @NotNull Text tooltip();

    /**
     * Creates a builder to construct a {@link ConfigCategory}
     */
    static Builder createBuilder() {
        return new ConfigCategoryImpl.BuilderImpl();
    }

    interface Builder {
        /**
         * Sets name of the category
         *
         * @see ConfigCategory#name()
         */
        Builder name(@NotNull Text name);

        /**
         * Adds an option to the root group of the category.
         * To add to another group, use {@link Builder#group(OptionGroup)}.
         * To construct an option, use {@link Option#createBuilder(Class)}
         *
         * @see ConfigCategory#groups()
         * @see OptionGroup#isRoot()
         */
        Builder option(@NotNull Option<?> option);

        /**
         * Adds multiple options to the root group of the category.
         * To add to another group, use {@link Builder#groups(Collection)}.
         * To construct an option, use {@link Option#createBuilder(Class)}
         *
         * @see ConfigCategory#groups()
         * @see OptionGroup#isRoot()
         */
        Builder options(@NotNull Collection<Option<?>> options);

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
        Builder tooltip(@NotNull Text... tooltips);

        ConfigCategory build();
    }
}
