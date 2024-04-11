package dev.isxander.yacl3.mixin;

import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TabNavigationBar.class)
public interface TabNavigationBarAccessor {
    /*? if >1.20.4 {*//*
    @Accessor
    net.minecraft.client.gui.layouts.LinearLayout getLayout();
    *//*? } else {*/
    @Accessor
    net.minecraft.client.gui.layouts.GridLayout getLayout();
    /*?}*/
}
