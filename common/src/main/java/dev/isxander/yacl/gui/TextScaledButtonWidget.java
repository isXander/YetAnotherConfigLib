package dev.isxander.yacl.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
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

    @Override
    public void renderString(GuiGraphics graphics, Font textRenderer, int color) {
        Font font = Minecraft.getInstance().font;
        PoseStack pose = graphics.pose();

        pose.pushPose();
        pose.translate(((this.getX() + this.width / 2f) - font.width(getMessage()) * textScale / 2), (float)this.getY() + (this.height - 8 * textScale) / 2f / textScale, 0);
        pose.scale(textScale, textScale, 1);
        graphics.drawString(font, getMessage(), 0, 0, color | Mth.ceil(this.alpha * 255.0F) << 24, true);
        pose.popPose();
    }
}
