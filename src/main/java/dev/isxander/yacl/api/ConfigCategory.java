package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.ConfigCategoryImpl;
import dev.isxander.yacl.impl.OptionGroupImpl;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

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
     * Creates a builder to construct a {@link ConfigCategory}
     */
    static Builder createBuilder() {
        return new Builder();
    }

    class Builder {
        private Text name;
        private final List<Option<?>> rootOptions = new ArrayList<>();

        private final List<OptionGroup> groups = new ArrayList<>();

        private Builder() {

        }

        /**
         * Sets name of the category
         *
         * @see ConfigCategory#name()
         */
        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        /**
         * Adds an option to the root group of the category.
         * To add to another group, use {@link Builder#group(OptionGroup)}.
         * To construct an option, use {@link Option#createBuilder(Class)}
         *
         * @see ConfigCategory#groups()
         * @see OptionGroup#isRoot()
         */
        public Builder option(@NotNull Option<?> option) {
            Validate.notNull(option, "`option` must not be null");

            this.rootOptions.add(option);
            return this;
        }

        /**
         * Adds multiple options to the root group of the category.
         * To add to another group, use {@link Builder#groups(Collection)}.
         * To construct an option, use {@link Option#createBuilder(Class)}
         *
         * @see ConfigCategory#groups()
         * @see OptionGroup#isRoot()
         */
        public Builder options(@NotNull Collection<Option<?>> options) {
            Validate.notEmpty(options, "`options` must not be empty");

            this.rootOptions.addAll(options);
            return this;
        }

        /**
         * Adds an option group.
         * To add an option to the root group, use {@link Builder#option(Option)}
         * To construct a group, use {@link OptionGroup#createBuilder()}
         */
        public Builder group(@NotNull OptionGroup group) {
            Validate.notNull(group, "`group` must not be null");

            this.groups.add(group);
            return this;
        }

        /**
         * Adds multiple option groups.
         * To add multiple options to the root group, use {@link Builder#options(Collection)}
         * To construct a group, use {@link OptionGroup#createBuilder()}
         */
        public Builder groups(@NotNull Collection<OptionGroup> groups) {
            Validate.notEmpty(groups, "`groups` must not be empty");

            this.groups.addAll(groups);
            return this;
        }

        public ConfigCategory build() {
            Validate.notNull(name, "`name` must not be null to build `ConfigCategory`");
            Validate.notEmpty(rootOptions, "`at least one option must be added to build `ConfigCategory`");

            List<OptionGroup> combinedGroups = new ArrayList<>();
            combinedGroups.add(new OptionGroupImpl(Text.empty(), ImmutableList.copyOf(rootOptions), true));
            combinedGroups.addAll(groups);

            return new ConfigCategoryImpl(name, ImmutableList.copyOf(combinedGroups));
        }
    }
}
