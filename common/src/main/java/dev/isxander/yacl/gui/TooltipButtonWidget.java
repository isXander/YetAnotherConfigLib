package dev.isxander.yacl.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TooltipButtonWidget extends Button {

    protected final Screen screen;
    protected MultiLineLabel wrappedDescription = null;

    public TooltipButtonWidget(Screen screen, int x, int y, int width, int height, Component message, Component tooltip, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.screen = screen;
        if (tooltip != null)
            setTooltip(tooltip);
    }

    public void renderHoveredTooltip(GuiGraphics graphics) {
        if (isHoveredOrFocused() && wrappedDescription != null) {
            YACLScreen.renderMultilineTooltip(graphics, Minecraft.getInstance().font, wrappedDescription, getX() + width / 2, getY() - 4, getY() + height + 4, screen.width, screen.height);
        }
    }

    public void setTooltip(Component tooltip) {
        wrappedDescription = MultiLineLabel.create(Minecraft.getInstance().font, tooltip, screen.width / 3 - 5);
    }
}
