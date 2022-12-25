package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ListOption;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ApiStatus.Internal
public final class OptionGroupImpl implements OptionGroup {
    private final @NotNull Text name;
    private final @NotNull Text tooltip;
    private final ImmutableList<? extends Option<?>> options;
    private final boolean collapsed;
    private final boolean isRoot;

    public OptionGroupImpl(@NotNull Text name, @NotNull Text tooltip, ImmutableList<? extends Option<?>> options, boolean collapsed, boolean isRoot) {
        this.name = name;
        this.tooltip = tooltip;
        this.options = options;
        this.collapsed = collapsed;
        this.isRoot = isRoot;
    }

    @Override
    public @NotNull Text name() {
        return name;
    }

    @Override
    public @NotNull Text tooltip() {
        return tooltip;
    }

    @Override
    public @NotNull ImmutableList<? extends Option<?>> options() {
        return options;
    }

    @Override
    public boolean collapsed() {
        return collapsed;
    }

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    @ApiStatus.Internal
    public static final class BuilderImpl implements OptionGroup.Builder {
        private Text name = Text.empty();
        private final List<Text> tooltipLines = new ArrayList<>();
        private final List<Option<?>> options = new ArrayList<>();
        private boolean collapsed = false;

        @Override
        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` must not be null");

            this.name = name;
            return this;
        }

        @Override
        public Builder tooltip(@NotNull Text... tooltips) {
            Validate.notEmpty(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        @Override
        public Builder option(@NotNull Option<?> option) {
            Validate.notNull(option, "`option` must not be null");

            if (option instanceof ListOption<?>)
                throw new UnsupportedOperationException("List options must not be added as an option but a group!");

            this.options.add(option);
            return this;
        }

        @Override
        public Builder options(@NotNull Collection<? extends Option<?>> options) {
            Validate.notEmpty(options, "`options` must not be empty");

            if (options.stream().anyMatch(ListOption.class::isInstance))
                throw new UnsupportedOperationException("List options must not be added as an option but a group!");

            this.options.addAll(options);
            return this;
        }

        @Override
        public Builder collapsed(boolean collapsible) {
            this.collapsed = collapsible;
            return this;
        }

        @Override
        public OptionGroup build() {
            Validate.notEmpty(options, "`options` must not be empty to build `OptionGroup`");

            MutableText concatenatedTooltip = Text.empty();
            boolean first = true;
            for (Text line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new OptionGroupImpl(name, concatenatedTooltip, ImmutableList.copyOf(options), collapsed, false);
        }
    }
}
