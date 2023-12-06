package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApiStatus.Internal
public final class ConfigCategoryImpl implements ConfigCategory {
    private final Component name;
    private final ImmutableList<OptionGroup> groups;
    private final Component tooltip;

    public ConfigCategoryImpl(Component name, ImmutableList<OptionGroup> groups, Component tooltip) {
        this.name = name;
        this.groups = groups;
        this.tooltip = tooltip;
    }

    @Override
    public @NotNull Component name() {
        return name;
    }

    @Override
    public @NotNull ImmutableList<OptionGroup> groups() {
        return groups;
    }

    @Override
    public @NotNull Component tooltip() {
        return tooltip;
    }

    @ApiStatus.Internal
    public static final class BuilderImpl implements Builder {
        private Component name;

        private final List<Option<?>> rootOptions = new ArrayList<>();
        private final List<OptionGroup> groups = new ArrayList<>();

        private final List<Component> tooltipLines = new ArrayList<>();

        @Override
        public Builder name(@NotNull Component name) {
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
        public Builder options(@NotNull Collection<? extends Option<?>> options) {
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
        public Builder tooltip(@NotNull Component... tooltips) {
            Validate.notEmpty(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        @Override
        public ConfigCategory build() {
            Validate.notNull(name, "`name` must not be null to build `ConfigCategory`");

            List<OptionGroup> combinedGroups = new ArrayList<>();
            combinedGroups.add(new OptionGroupImpl(CommonComponents.EMPTY, OptionDescription.EMPTY, ImmutableList.copyOf(rootOptions), false, true));
            combinedGroups.addAll(groups);

            Validate.notEmpty(combinedGroups, "at least one option must be added to build `ConfigCategory`");

            MutableComponent concatenatedTooltip = Component.empty();
            boolean first = true;
            for (Component line : tooltipLines) {
                if (line.getContents() == PlainTextContents.EMPTY)
                    continue;

                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new ConfigCategoryImpl(name, ImmutableList.copyOf(combinedGroups), concatenatedTooltip);
        }
    }
}
