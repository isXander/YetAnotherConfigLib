package dev.isxander.yacl3.gui;

import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TextScaledButtonWidget extends TooltipButtonWidget {
    public float textScale;

    public TextScaledButtonWidget(Screen screen, int x, int y, int width, int height, float textScale, Component message, Component tooltip, OnPress onPress) {
        super(screen, x, y, width, height, message, tooltip, onPress);
        this.textScale = textScale;
    }

    public TextScaledButtonWidget(Screen screen, int x, int y, int width, int height, float textScale, Component message, OnPress onPress) {
        this(screen, x, y, width, height, textScale, message, null, onPress);
    }

    // FIXME: currently the text just doesn't render.
    //? if >=1.21.11 {
    @Override
    protected void renderDefaultLabel(ActiveTextCollector activeTextCollector) {
        activeTextCollector.defaultParameters(activeTextCollector.defaultParameters().withScale(textScale));
        super.renderDefaultLabel(activeTextCollector);
    }
    //?} else {
    /*@Override
    public void renderString(GuiGraphics graphics, Font textRenderer, int color) {
        Font font = Minecraft.getInstance().font;

        GuiUtils.pushPose(graphics);
        GuiUtils.translate2D(graphics, ((this.getX() + this.width / 2f) - font.width(getMessage()) * textScale / 2), (float)this.getY() + (this.height - 8 * textScale) / 2f / textScale);
        GuiUtils.scale2D(graphics, textScale, textScale);
        graphics.drawString(font, getMessage(), 0, 0, color | Mth.ceil(this.alpha * 255.0F) << 24, true);
        GuiUtils.popPose(graphics);
    }
    *///?}
}
