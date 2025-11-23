package dev.isxander.yacl3.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TooltipButtonWidget extends /*? if >=1.21.11 {*/Button.Plain/*?} else {*//*Button*//*?}*/ {

    protected final Screen screen;

    public TooltipButtonWidget(Screen screen, int x, int y, int width, int height, Component message, Component tooltip, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.screen = screen;
        if (tooltip != null)
            setTooltip(Tooltip.create(tooltip));
    }
}
