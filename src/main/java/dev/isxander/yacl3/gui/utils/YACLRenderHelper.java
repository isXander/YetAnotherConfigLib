package dev.isxander.yacl3.gui.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class YACLRenderHelper {
    /*? if >1.20.1 {*/
    private static final net.minecraft.client.gui.components.WidgetSprites SPRITES = new net.minecraft.client.gui.components.WidgetSprites(
            YACLPlatform.mcRl("widget/button"), // normal
            YACLPlatform.mcRl("widget/button_disabled"), // disabled & !focused
            YACLPlatform.mcRl("widget/button_highlighted"), // !disabled & focused
            YACLPlatform.mcRl("widget/slider_highlighted") // disabled & focused
    );
    /*?} else {*/
    /*private static final ResourceLocation SLIDER_LOCATION = new ResourceLocation("textures/gui/slider.png");
    *//*?}*/

    public static void renderButtonTexture(GuiGraphics graphics, int x, int y, int width, int height, boolean enabled, boolean focused) {
        /*? if >1.20.1 {*/
        graphics.blitSprite(SPRITES.get(enabled, focused), x, y, width, height);
        /*?} else {*/
        /*int textureV;
        if (enabled) {
            textureV = focused ? 60 : 40;
        } else {
            textureV = focused ? 20 : 0;
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        graphics.blitNineSliced(SLIDER_LOCATION, x, y, width, height, 20, 4, 200, 20, 0, textureV);
        *//*?}*/
    }

    public static ResourceLocation getSpriteLocation(String path) {
        /*? if >1.20.3 {*/
        return YACLPlatform.rl(path);
        /*?} else {*/
        /*return YACLPlatform.rl("textures/gui/sprites/" + path + ".png");
        *//*?}*/
    }
}
