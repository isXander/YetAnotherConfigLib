package dev.isxander.yacl3.gui;

import net.minecraft.client.Minecraft;
//? if >=1.21.11 {
import org.jspecify.annotations.NonNull;
//?}
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

    //? if >=1.21.11 {
    @Override
    protected void renderContents(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderDefaultSprite(guiGraphics);

        if (Math.abs(textScale - 1.0f) < 0.01f) {
            this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
            return;
        }

        Font font = Minecraft.getInstance().font;
        Component message = getMessage();

        float scaledX = getX() + getWidth() / 2.0f - (font.width(message) * textScale) / 2.0f;
        float scaledY = getY() + getHeight() / 2.0f - (font.lineHeight * textScale) / 2.0f;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(scaledX, scaledY);
        guiGraphics.pose().scale(textScale, textScale);

        int color = this.active ? 0xFFFFFFFF : 0xFFA0A0A0;
        guiGraphics.drawString(font, message, 0, 0, color | Mth.ceil(this.alpha * 255.0F) << 24, true);
        guiGraphics.pose().popMatrix();
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
