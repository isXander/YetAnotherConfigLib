package dev.isxander.yacl3.gui;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;

public class YACLTooltip extends Tooltip {
    private final net.minecraft.client.gui.components.AbstractWidget widget;

    public YACLTooltip(Component tooltip, net.minecraft.client.gui.components.AbstractWidget widget) {
        super(tooltip, tooltip);
        this.widget = widget;
    }

    /*? if >1.20.4 {*//* // stonecutter cannot handle AND expressions
    *//*? } elif >1.20.1 {*/
    @Override
    protected ClientTooltipPositioner createTooltipPositioner(boolean bl, boolean bl2, ScreenRectangle screenRectangle) {
        return new YACLTooltipPositioner(widget);
    }
    /*?}*/
}
