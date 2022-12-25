package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.ListOption;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.impl.utils.YACLConstants;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApiStatus.Internal
public final class ConfigCategoryImpl implements ConfigCategory {
    private final Text name;
    private final ImmutableList<OptionGroup> groups;
    private final Text tooltip;

    public ConfigCategoryImpl(Text name, ImmutableList<OptionGroup> groups, Text tooltip) {
        this.name = name;
        this.groups = groups;
        this.tooltip = tooltip;
    }

    @Override
    public @NotNull Text name() {
        return name;
    }

    @Override
    public @NotNull ImmutableList<OptionGroup> groups() {
        return groups;
    }

    @Override
    public @NotNull Text tooltip() {
        return tooltip;
    }

    @ApiStatus.Internal
    public static final class BuilderImpl implements Builder {
        private Text name;

        private final List<Option<?>> rootOptions = new ArrayList<>();
        private final List<OptionGroup> groups = new ArrayList<>();

        private final List<Text> tooltipLines = new ArrayList<>();

        @Override
        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        @Override
        public Builder option(@NotNull Option<?> option) {
            Validate.notNull(option, "`option` must not be null");

            if (option instanceof ListOption<?> listOption) {
                YACLConstants.LOGGER.warn("Adding list option as an option is not supported! Rerouting to group!");
                return group(listOption);
            }

            this.rootOptions.add(option);
            return this;
        }

        @Override
        public Builder options(@NotNull Collection<Option<?>> options) {
            Validate.notNull(options, "`options` must not be null");

            if (options.stream().anyMatch(ListOption.class::isInstance))
                throw new UnsupportedOperationException("List options must not be added as an option but a group!");

            this.rootOptions.addAll(options);
            return this;
        }

        @Override
        public Builder group(@NotNull OptionGroup group) {
            Validate.notNull(group, "`group` must not be null");

            this.groups.add(group);
            return this;
        }

        @Override
        public Builder groups(@NotNull Collection<OptionGroup> groups) {
            Validate.notEmpty(groups, "`groups` must not be empty");

            this.groups.addAll(groups);
            return this;
        }

        @Override
        public Builder tooltip(@NotNull Text... tooltips) {
            Validate.notEmpty(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        @Override
        public ConfigCategory build() {
            Validate.notNull(name, "`name` must not be null to build `ConfigCategory`");

            List<OptionGroup> combinedGroups = new ArrayList<>();
            combinedGroups.add(new OptionGroupImpl(Text.empty(), Text.empty(), ImmutableList.copyOf(rootOptions), false, true));
            combinedGroups.addAll(groups);

            Validate.notEmpty(combinedGroups, "at least one option must be added to build `ConfigCategory`");

            MutableText concatenatedTooltip = Text.empty();
            boolean first = true;
            for (Text line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new ConfigCategoryImpl(name, ImmutableList.copyOf(combinedGroups), concatenatedTooltip);
        }
    }
}
