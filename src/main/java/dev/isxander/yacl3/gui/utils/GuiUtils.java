package dev.isxander.yacl3.gui.utils;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import dev.isxander.yacl3.debug.DebugProperties;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.2
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.util.function.Consumer;
import java.util.function.Function;

//? if >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
*///?} elif >=1.21.2 {
import net.minecraft.client.renderer.RenderStateShard;
//?} else {
/*import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.GameRenderer;
*///?}

public class GuiUtils {
    //? if <1.21.2 {
    /*private static Function<ResourceLocation, RenderType> GUI_TEXTURED = Util.memoize(
            location -> RenderType.create(
                    "yacl:gui_textured",
                    DefaultVertexFormat.POSITION_TEX_COLOR,
                    VertexFormat.Mode.QUADS,
                    786432,
                    RenderType.CompositeState.builder()
                            .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                            .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                            .createCompositeState(false)
            )
    );
    *///?}

    //? if >=1.21.2 && <1.21.6 {
    public static Function<ResourceLocation, RenderType> GUI_TEXTURED_FILTERED = Util.memoize(
            location -> RenderType.create(
                    "yacl:gui_textured_filtered",
                    //? if <1.21.5 {
                    /*DefaultVertexFormat.POSITION_TEX_COLOR,
                    VertexFormat.Mode.QUADS,
                    *///?}
                    786432,
                    //? if >=1.21.5 {
                    net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                    //?}
                    RenderType.CompositeState.builder()
                            .setTextureState(new RenderType.TextureStateShard(location, net.minecraft.util.TriState.TRUE, false))
                            //? if <1.21.5 {
                            /*.setShaderState(RenderStateShard.POSITION_TEXTURE_COLOR_SHADER)
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                            *///?}
                            .createCompositeState(false)
            )
    );
    //?}

    public static void pushPose(GuiGraphics graphics) {
        //? if >=1.21.6 {
        /*graphics.pose().pushMatrix();
        *///?} else {
        graphics.pose().pushPose();
        //?}
    }

    public static void popPose(GuiGraphics graphics) {
        //? if >=1.21.6 {
        /*graphics.pose().popMatrix();
        *///?} else {
        graphics.pose().popPose();
        //?}
    }

    public static void translate2D(GuiGraphics graphics, float x, float y) {
        //? if >=1.21.6 {
        /*graphics.pose().translate(x, y);
        *///?} else {
        graphics.pose().translate(x, y, 0);
        //?}
    }

    public static void translateZ(GuiGraphics graphics, float z) {
        //? if <1.21.6 {
        graphics.pose().translate(0, 0, z);
        //?}
    }

    public static void scale2D(GuiGraphics graphics, float x, float y) {
        //? if >=1.21.6 {
        /*graphics.pose().scale(x, y);
        *///?} else {
        graphics.pose().scale(x, y, 1);
        //?}
    }

    public static void rotate2D(GuiGraphics graphics, float angle) {
        //? if >=1.21.6 {
        /*graphics.pose().rotate(angle * Mth.DEG_TO_RAD);
        *///?} else {
        graphics.pose().rotateAround(Axis.ZP.rotationDegrees(angle), 0, 0, 1);
        //?}
    }

    public static void blitGuiTex(GuiGraphics graphics, ResourceLocation texture, int x, int y, float u, float v, int textureWidth, int textureHeight, int width, int height) {
        blitGuiTex(graphics, texture, x, y, u, v, textureWidth, textureHeight, width, height, false);
    }

    public static void blitGuiTex(GuiGraphics graphics, ResourceLocation texture, int x, int y, float u, float v, int textureWidth, int textureHeight, int width, int height, boolean linearFiltering) {
        //? if <1.21.2
        /*doTextureFiltering();*/

        graphics.blit(
                //? if >=1.21.2
                guiTextured(linearFiltering),
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
                guiTextured(false),
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

    public static void blitSprite(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height) {
        graphics.blitSprite(
                //? if >=1.21.2
                guiTextured(false),
                sprite,
                x, y,
                width, height
        );
    }

    //? if >=1.21.6 {
    /*public static RenderPipeline guiTextured(boolean textureFiltering) {
        // in 1.21.6, texture filtering is done on the texture level on resource reload, ignored
        return RenderPipelines.GUI_TEXTURED;
    }
    *///?} elif >=1.21.2 {
    public static Function<ResourceLocation, RenderType> guiTextured(boolean textureFiltering) {
        return textureFiltering ? GUI_TEXTURED_FILTERED : RenderType::guiTextured;
    }
    //?} else {
    /*public static Function<ResourceLocation, RenderType> guiTextured(boolean textureFiltering) {
        return GUI_TEXTURED;
    }
    *///?}

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

    // On new versions this should be done via the render pipeline. Setting global state will not work.
    //? if <1.21.2 {
    /*public static void doTextureFiltering() {
        if (DebugProperties.IMAGE_FILTERING) {
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_LINEAR);
        }
    }
    *///?}

    public static int extractAlpha(int argb) {
        //? if >=1.21.2 {
        return ARGB.alpha(argb);
        //?} else {
        /*return (argb >> 24) & 0xFF;
        *///?}
    }

    public static int putAlpha(int rgb, int alpha) {
        //? if >=1.21.2 {
        return ARGB.color(alpha, rgb);
        //?} else {
        /*return (rgb & 0x00FFFFFF) | (alpha << 24);
        *///?}
    }
}
