package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.PlaceholderCategory;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public record PlaceholderCategoryImpl(Text name, BiFunction<MinecraftClient, YACLScreen, Screen> screen, Text tooltip) implements PlaceholderCategory {
    @Override
    public @NotNull ImmutableList<OptionGroup> groups() {
        return ImmutableList.of();
    }
}
