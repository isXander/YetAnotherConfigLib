package dev.isxander.yacl.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class TextScaledButtonWidget extends Button {
    public float textScale;

    public TextScaledButtonWidget(int x, int y, int width, int height, float textScale, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, NO_TOOLTIP);
        this.textScale = textScale;
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        // prevents super from rendering text
        Component message = getMessage();
        setMessage(Component.empty());

        super.renderButton(matrices, mouseX, mouseY, delta);

        setMessage(message);
        int j = this.active ? 16777215 : 10526880;
        FormattedCharSequence orderedText = getMessage().getVisualOrderText();
        Font font = Minecraft.getInstance().font;

        matrices.pushPose();
        matrices.translate(((this.x + this.width / 2f) - font.width(orderedText) * textScale / 2), (float)this.y + (this.height - 8 * textScale) / 2f / textScale, 0);
        matrices.scale(textScale, textScale, 1);
        font.drawShadow(matrices, orderedText, 0, 0, j | Mth.ceil(this.alpha * 255.0F) << 24);
        matrices.popPose();
    }
}
