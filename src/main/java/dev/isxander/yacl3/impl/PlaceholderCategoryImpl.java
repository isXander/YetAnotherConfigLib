package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.PlaceholderCategory;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@ApiStatus.Internal
public final class PlaceholderCategoryImpl implements PlaceholderCategory {
    private final Component name;
    private final BiFunction<Minecraft, YACLScreen, Screen> screen;
    private final Component tooltip;

    public PlaceholderCategoryImpl(Component name, BiFunction<Minecraft, YACLScreen, Screen> screen, Component tooltip) {
        this.name = name;
        this.screen = screen;
        this.tooltip = tooltip;
    }

    @Override
    public @NotNull ImmutableList<OptionGroup> groups() {
        return ImmutableList.of();
    }

    @Override
    public @NotNull Component name() {
        return name;
    }

    @Override
    public BiFunction<Minecraft, YACLScreen, Screen> screen() {
        return screen;
    }

    @Override
    public @NotNull Component tooltip() {
        return tooltip;
    }

    @ApiStatus.Internal
    public static final class BuilderImpl implements Builder {
        private Component name;

        private final List<Component> tooltipLines = new ArrayList<>();

        private BiFunction<Minecraft, YACLScreen, Screen> screenFunction;

        @Override
        public Builder name(@NotNull Component name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        @Override
        public Builder tooltip(@NotNull Component... tooltips) {
            Validate.notEmpty(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        @Override
        public Builder screen(@NotNull BiFunction<Minecraft, YACLScreen, Screen> screenFunction) {
            Validate.notNull(screenFunction, "`screenFunction` cannot be null");

            this.screenFunction = screenFunction;
            return this;
        }

        @Override
        public PlaceholderCategory build() {
            Validate.notNull(name, "`name` must not be null to build `ConfigCategory`");

            MutableComponent concatenatedTooltip = Component.empty();
            boolean first = true;
            for (Component line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new PlaceholderCategoryImpl(name, screenFunction, concatenatedTooltip);
        }
    }
}
