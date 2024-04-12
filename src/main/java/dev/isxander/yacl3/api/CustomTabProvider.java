package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;

/**
 * Allows categories to provide custom tab windows that replaces the
 * regular YACL options screen. The tabs at the top will remain visible,
 * but you can now provide custom tab content for richer configurations.
 * <p>
 * Part of the GUI API: could change with minecraft updates and is not stable
 */
public interface CustomTabProvider {
    Tab createTab(YACLScreen screen, ScreenRectangle tabArea);
}
