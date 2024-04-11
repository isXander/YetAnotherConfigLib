package dev.isxander.yacl3.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TabNavigationBar.class)
public interface TabNavigationBarAccessor {
    /*? if >1.20.4 {*//*
    @Accessor
    net.minecraft.client.gui.layouts.LinearLayout getLayout();
    *//*? } else {*/
    @Accessor
    net.minecraft.client.gui.layouts.GridLayout getLayout();
    /*?}*/

    @Accessor
    int getWidth();

    @Accessor
    TabManager getTabManager();

    @Accessor
    ImmutableList<Tab> getTabs();

    @Accessor
    ImmutableList<TabButton> getTabButtons();

}
