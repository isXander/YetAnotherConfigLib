package dev.isxander.yacl3.gui.tab;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TabExt extends Tab {
    @Nullable Tooltip getTooltip();

    default void tick() {}

    default void renderBackground(GuiGraphics graphics) {}

    //? if >=1.21.11
    @Override
    default @NotNull Component getTabExtraNarration() {
        return Component.empty();
    }
}
