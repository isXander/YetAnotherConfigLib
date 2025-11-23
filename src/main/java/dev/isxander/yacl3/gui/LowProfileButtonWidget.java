package dev.isxander.yacl3.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class LowProfileButtonWidget extends /*? if >=1.21.11 {*/Button.Plain/*?} else {*//*Button*//*?}*/ {
    public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, Tooltip tooltip) {
        this(x, y, width, height, message, onPress);
        setTooltip(tooltip);
    }

    //? if >=1.21.11 {
    @Override
    protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
        if (isHoveredOrFocused() && isActive()) {
            this.renderDefaultSprite(guiGraphics);
        }
        this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
    }
    //?} else {
    /*@Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        if (!isHoveredOrFocused() || !active) {
            int j = this.active ? 0xFFFFFFFF : 0xFFA0A0A0;
            this.renderString(graphics, Minecraft.getInstance().font, j);
        } else {
            super.renderWidget(graphics, mouseX, mouseY, deltaTicks);
        }
    }
    *///?}
}
