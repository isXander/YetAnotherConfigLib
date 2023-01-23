package dev.isxander.yacl.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TooltipButtonWidget extends TextScaledButtonWidget {

    protected final Screen screen;
    protected MultiLineLabel wrappedDescription;

    public TooltipButtonWidget(Screen screen, int x, int y, int width, int height, Component message, float textScale, Component tooltip, Button.OnPress onPress) {
        super(x, y, width, height, textScale, message, onPress);
        this.screen = screen;
        setTooltip(tooltip);
    }

    public TooltipButtonWidget(Screen screen, int x, int y, int width, int height, Component message, Component tooltip, Button.OnPress onPress) {
        this(screen, x, y, width, height, message, 1, tooltip, onPress);
    }

    public void renderHoveredTooltip(PoseStack matrices) {
        if (isHoveredOrFocused()) {
            YACLScreen.renderMultilineTooltip(matrices, Minecraft.getInstance().font, wrappedDescription, x + width / 2, y - 4, y + height + 4, screen.width, screen.height);
        }
    }

    public void setTooltip(Component tooltip) {
        wrappedDescription = MultiLineLabel.create(Minecraft.getInstance().font, tooltip, screen.width / 3 - 5);
    }
}
