package dev.isxander.yacl3.gui.tab;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public interface TabExt extends Tab {
    @Nullable Tooltip getTooltip();

    default void tick() {}

    default void renderBackground(GuiGraphicsExtractor graphics) {}

    @Override
    default @NotNull Component getTabExtraNarration() {
        return Component.empty();
    }

    //? if >=26.2 {
    @Override
    default @NonNull Layout getLayout() {
        return new FrameLayout();
    }
    //?}
}
