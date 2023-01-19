package dev.isxander.yacl.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class LowProfileButtonWidget extends Button {
    public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, Tooltip tooltip) {
        this(x, y, width, height, message, onPress);
        setTooltip(tooltip);
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (!isHoveredOrFocused() || !active) {
            int j = this.active ? 0xFFFFFF : 0xA0A0A0;
            drawCenteredString(matrices, Minecraft.getInstance().font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        } else {
            super.renderButton(matrices, mouseX, mouseY, delta);
        }
    }
}
