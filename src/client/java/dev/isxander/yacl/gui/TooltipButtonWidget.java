package dev.isxander.yacl.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class TooltipButtonWidget extends TextScaledButtonWidget {

    protected final Screen screen;
    protected MultilineText wrappedDescription;

    public TooltipButtonWidget(Screen screen, int x, int y, int width, int height, Text message, float textScale, Text tooltip, PressAction onPress) {
        super(x, y, width, height, textScale, message, onPress);
        this.screen = screen;
        setTooltip(tooltip);
    }

    public TooltipButtonWidget(Screen screen, int x, int y, int width, int height, Text message, Text tooltip, PressAction onPress) {
        this(screen, x, y, width, height, message, 1, tooltip, onPress);
    }

    public void renderHoveredTooltip(MatrixStack matrices) {
        if (isHovered()) {
            YACLScreen.renderMultilineTooltip(matrices, MinecraftClient.getInstance().textRenderer, wrappedDescription, getX() + width / 2, getY() - 4, getY() + height + 4, screen.width, screen.height);
        }
    }

    public void setTooltip(Text tooltip) {
        wrappedDescription = MultilineText.create(MinecraftClient.getInstance().textRenderer, tooltip, screen.width / 3 - 5);
    }
}
