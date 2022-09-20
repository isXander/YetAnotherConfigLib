package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public record YetAnotherConfigLibImpl(Text title, ImmutableList<ConfigCategory> categories, Runnable saveFunction, Consumer<YACLScreen> initConsumer) implements YetAnotherConfigLib {
    @Override
    public Screen generateScreen(Screen parent) {
        return new YACLScreen(this, parent);
    }
}
