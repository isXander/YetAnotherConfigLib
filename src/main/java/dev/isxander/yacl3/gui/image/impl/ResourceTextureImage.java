package dev.isxander.yacl3.gui.image.impl;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import dev.isxander.yacl3.debug.DebugProperties;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ResourceTextureImage implements ImageRenderer {
    private final ResourceLocation location;
    private final int width, height;
    private final int textureWidth, textureHeight;
    private final float u, v;

    public ResourceTextureImage(ResourceLocation location, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        this.location = location;
        this.width = width;
        this.height = height;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta) {
        float ratio = renderWidth / (float)this.width;
        int targetHeight = (int) (this.height * ratio);

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(ratio, ratio, 1);

        if (DebugProperties.IMAGE_FILTERING) {
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_LINEAR);
            GlStateManager._texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_LINEAR);
        }

        graphics.blit(location, 0, 0, this.u, this.v, this.width, this.height, this.textureWidth, this.textureHeight);

        graphics.pose().popPose();

        return targetHeight;
    }

    @Override
    public void close() {

    }

    public static ImageRendererFactory createFactory(ResourceLocation location, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        return (ImageRendererFactory.OnThread) () -> () -> new ResourceTextureImage(location, u, v, width, height, textureWidth, textureHeight);
    }
}
