package dev.isxander.yacl3.gui.image.impl;

import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererFactory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ResourceTextureImage implements ImageRenderer {
    private final Identifier location;
    private final int width, height;
    private final int textureWidth, textureHeight;
    private final float u, v;

    public ResourceTextureImage(Identifier location, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        this.location = location;
        this.width = width;
        this.height = height;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
    }

    @Override
    public int render(GuiGraphicsExtractor graphics, int x, int y, int renderWidth, float tickDelta) {
        float ratio = renderWidth / (float)this.width;
        int targetHeight = (int) (this.height * ratio);

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(ratio, ratio);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                location,
                0, 0,
                this.u, this.v,
                this.width, this.height,
                this.textureWidth, this.textureHeight
        );

        graphics.pose().popMatrix();

        return targetHeight;
    }

    @Override
    public void close() {

    }

    public static ImageRendererFactory createFactory(Identifier location, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        return (ImageRendererFactory.OnThread) () -> () -> new ResourceTextureImage(location, u, v, width, height, textureWidth, textureHeight);
    }
}
