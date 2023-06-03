package dev.isxander.yacl3.gui.tab;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.Tab;
import org.jetbrains.annotations.Nullable;

public interface TabExt extends Tab {
    @Nullable Tooltip getTooltip();
}
