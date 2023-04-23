package dev.isxander.yacl.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class LowProfileButtonWidget extends Button {
    public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, Tooltip tooltip) {
        this(x, y, width, height, message, onPress);
        setTooltip(tooltip);
    }

    @Override
    public void renderWidget(PoseStack matrices, int mouseX, int mouseY, float deltaTicks) {
        if (!isHoveredOrFocused() || !active) {
            int j = this.active ? 0xFFFFFF : 0xA0A0A0;
            this.renderString(matrices, Minecraft.getInstance().font, j);
        } else {
            super.renderWidget(matrices, mouseX, mouseY, deltaTicks);
        }
    }
}
