package dev.isxander.yacl3.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class LowProfileButtonWidget extends Button.Plain {
    public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    public LowProfileButtonWidget(int x, int y, int width, int height, Component message, OnPress onPress, Tooltip tooltip) {
        this(x, y, width, height, message, onPress);
        setTooltip(tooltip);
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics/*? if <26.4 {*//*, int mouseX, int mouseY, float a*//*?}*/) {
        if (/*? if >=26.4 {*/(isHovered() || isFocused())/*?} else {*//*isHoveredOrFocused()*//*?}*/ && isActive()) {
            this.extractDefaultSprite(graphics);
        }
        this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
    }
}
