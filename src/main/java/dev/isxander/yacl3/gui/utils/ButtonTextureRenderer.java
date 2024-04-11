package dev.isxander.yacl3.gui.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ButtonTextureRenderer {
    /*? if >1.20.1 {*/
    private static final net.minecraft.client.gui.components.WidgetSprites SPRITES = new net.minecraft.client.gui.components.WidgetSprites(
            new ResourceLocation("widget/button"), // normal
            new ResourceLocation("widget/button_disabled"), // disabled & !focused
            new ResourceLocation("widget/button_highlighted"), // !disabled & focused
            new ResourceLocation("widget/slider_highlighted") // disabled & focused
    );
    /*?} else {*//*
    private static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");
    *//*?}*/

    public static void render(GuiGraphics graphics, int x, int y, int width, int height, boolean enabled, boolean focused) {
        /*? if >1.20.1 {*/
        graphics.blitSprite(SPRITES.get(enabled, focused), x, y, width, height);
        /*?} else {*//*
        int textureV;
        if (enabled) {
            textureV = focused ? 60 : 40;
        } else {
            textureV = focused ? 20 : 0;
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        graphics.blitNineSliced(SLIDER_LOCATION, x, y, width, height, 20, 4, 200, 20, 0, textureV);
        *//*?}*/
    }
}
