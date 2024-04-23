package dev.isxander.yacl3.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TabNavigationBar.class)
public interface TabNavigationBarAccessor {
    /*? if >1.20.4 {*/
    @Accessor("layout")
    net.minecraft.client.gui.layouts.LinearLayout yacl$getLayout();
    /*? } else {*//*
    @Accessor("layout")
    net.minecraft.client.gui.layouts.GridLayout yacl$getLayout();
    *//*?}*/

    @Accessor("width")
    int yacl$getWidth();

    @Accessor("tabManager")
    TabManager yacl$getTabManager();

    @Accessor("tabs")
    ImmutableList<Tab> yacl$getTabs();

    @Accessor("tabButtons")
    ImmutableList<TabButton> yacl$getTabButtons();

}
