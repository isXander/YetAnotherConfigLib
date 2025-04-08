package dev.isxander.yacl3.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.LinearLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TabNavigationBar.class)
public interface TabNavigationBarAccessor {
    @Accessor("layout")
    LinearLayout yacl$getLayout();

    @Accessor("width")
    int yacl$getWidth();

    @Accessor("tabManager")
    TabManager yacl$getTabManager();

    @Accessor("tabs")
    ImmutableList<Tab> yacl$getTabs();

    @Accessor("tabButtons")
    ImmutableList<TabButton> yacl$getTabButtons();

}
