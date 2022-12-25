package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.PlaceholderCategory;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@ApiStatus.Internal
public final class PlaceholderCategoryImpl implements PlaceholderCategory {
    private final Text name;
    private final BiFunction<MinecraftClient, YACLScreen, Screen> screen;
    private final Text tooltip;

    public PlaceholderCategoryImpl(Text name, BiFunction<MinecraftClient, YACLScreen, Screen> screen, Text tooltip) {
        this.name = name;
        this.screen = screen;
        this.tooltip = tooltip;
    }

    @Override
    public @NotNull ImmutableList<OptionGroup> groups() {
        return ImmutableList.of();
    }

    @Override
    public @NotNull Text name() {
        return name;
    }

    @Override
    public BiFunction<MinecraftClient, YACLScreen, Screen> screen() {
        return screen;
    }

    @Override
    public @NotNull Text tooltip() {
        return tooltip;
    }

    @ApiStatus.Internal
    public static final class BuilderImpl implements PlaceholderCategory.Builder {
        private Text name;

        private final List<Text> tooltipLines = new ArrayList<>();

        private BiFunction<MinecraftClient, YACLScreen, Screen> screenFunction;

        @Override
        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

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
        public Builder screen(@NotNull BiFunction<MinecraftClient, YACLScreen, Screen> screenFunction) {
            Validate.notNull(screenFunction, "`screenFunction` cannot be null");

            this.screenFunction = screenFunction;
            return this;
        }

        @Override
        public PlaceholderCategory build() {
            Validate.notNull(name, "`name` must not be null to build `ConfigCategory`");

            MutableText concatenatedTooltip = Text.empty();
            boolean first = true;
            for (Text line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new PlaceholderCategoryImpl(name, screenFunction, concatenatedTooltip);
        }
    }
}
