package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record YetAnotherConfigLibImpl(Text title, ImmutableList<ConfigCategory> categories) implements YetAnotherConfigLib {
    @Override
    public Screen generateScreen() {
        return null;
    }
}
