package dev.isxander.yacl3.gui.utils;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class GuiUtils {
    public static void drawSpecial(GuiGraphics graphics, Consumer<MultiBufferSource> consumer) {
        //? if >=1.21.2 {
        graphics.drawSpecial(consumer);
        //?} else {
        /*MultiBufferSource.BufferSource bufferSource = graphics.bufferSource();
        consumer.accept(bufferSource);
        bufferSource.endBatch();
        *///?}
    }

    public static void blitGuiTex(GuiGraphics graphics, ResourceLocation texture, int x, int y, float u, float v, int textureWidth, int textureHeight, int width, int height) {
        graphics.blit(
                //? if >=1.21.2
                RenderType::guiTextured,
                texture,
                x, y,
                u, v,
                textureWidth, textureHeight,
                width, height
        );
    }

    public static void blitGuiTexColor(GuiGraphics graphics, ResourceLocation texture, int x, int y, float u, float v, int textureWidth, int textureHeight, int width, int height, int color) {
        //? if <1.21.2 {
        /*float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        graphics.setColor(r, g, b, a);
        *///?}
        graphics.blit(
                //? if >=1.21.2
                RenderType::guiTextured,
                texture,
                x, y,
                u, v,
                textureWidth, textureHeight,
                width, height
                //? if >=1.21.2
                ,color
        );
        //? if <1.21.2
        /*graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);*/
    }

    //? if >1.20.1 {
    public static void blitSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height) {
        graphics.blitSprite(
                //? if >=1.21.2
                RenderType::guiTextured,
                sprite,
                x, y,
                width, height
        );
    }
    //?}

    public static MutableComponent translatableFallback(String key, Component fallback) {
        if (Language.getInstance().has(key))
            return Component.translatable(key);
        return fallback.copy();
    }

    public static String shortenString(String string, Font font, int maxWidth, String suffix) {
        if (string.isEmpty())
            return string;

        boolean firstIter = true;
        while (font.width(string) > maxWidth) {
            string = string.substring(0, Math.max(string.length() - 1 - (firstIter ? 1 : suffix.length() + 1), 0)).trim();
            string += suffix;

            if (string.equals(suffix))
                break;

            firstIter = false;
        }

        return string;
    }


    public static void setPixelARGB(NativeImage nativeImage, int x, int y, int argb) {
        // In 1.21.2+, you set the pixel color in ARGB format, where it internally converts to ABGR.
        // Before this, you need to directly set the pixel color in ABGR format.

        //? if >=1.21.2 {
        nativeImage.setPixel(x, y, argb);
        //?} else {
        /*int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        int abgr = (a << 24) | (b << 16) | (g << 8) | r;
        nativeImage.setPixelRGBA(x, y, abgr); // method name is misleading. It's actually ABGR.
        *///?}
    }
}
