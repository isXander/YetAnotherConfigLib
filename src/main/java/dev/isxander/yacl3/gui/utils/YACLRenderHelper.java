package dev.isxander.yacl3.gui.utils;

import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.GuiGraphics;

public class YACLRenderHelper {
    private static final net.minecraft.client.gui.components.WidgetSprites SPRITES = new net.minecraft.client.gui.components.WidgetSprites(
            YACLPlatform.mcRl("widget/button"), // normal
            YACLPlatform.mcRl("widget/button_disabled"), // disabled & !focused
            YACLPlatform.mcRl("widget/button_highlighted"), // !disabled & focused
            YACLPlatform.mcRl("widget/slider_highlighted") // disabled & focused
    );

    public static void renderButtonTexture(GuiGraphics graphics, int x, int y, int width, int height, boolean enabled, boolean focused) {
        GuiUtils.blitSprite(graphics, SPRITES.get(enabled, focused), x, y, width, height);
    }
}
