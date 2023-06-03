package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
    public void renderString(PoseStack matrices, Font textRenderer, int color) {
        Font font = Minecraft.getInstance().font;

        matrices.pushPose();
        matrices.translate(((this.getX() + this.width / 2f) - font.width(getMessage()) * textScale / 2), (float)this.getY() + (this.height - 8 * textScale) / 2f / textScale, 0);
        matrices.scale(textScale, textScale, 1);
        font.drawShadow(matrices, getMessage(), 0, 0, color | Mth.ceil(this.alpha * 255.0F) << 24);
        matrices.popPose();
    }
}
