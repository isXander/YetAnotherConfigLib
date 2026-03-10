package dev.isxander.yacl3.gui.image.impl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

import java.io.FileInputStream;
import java.nio.file.Path;

public class DynamicTextureImage implements ImageRenderer {
    protected static final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

    protected NativeImage image;
    protected DynamicTexture texture;
    protected final Identifier uniqueLocation;
    protected final int width, height;
    protected final boolean textureFiltering;

    public DynamicTextureImage(NativeImage image, Identifier location, boolean textureFiltering) {
        RenderSystem.assertOnRenderThread();

        this.image = image;
        this.texture = new DynamicTexture(location::toString, image);
        this.textureFiltering = textureFiltering;
        this.uniqueLocation = location;
        textureManager.register(this.uniqueLocation, this.texture);
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    @Override
    public int render(GuiGraphicsExtractor graphics, int x, int y, int renderWidth, float tickDelta) {
        if (image == null) return 0;

        float ratio = renderWidth / (float)this.width;
        int targetHeight = (int) (this.height * ratio);

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(ratio, ratio);

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                uniqueLocation,
                0, 0,
                (float) 0, (float) 0,
                this.width, this.height,
                this.width, this.height
        );

        graphics.pose().popMatrix();

        return targetHeight;
    }

    @Override
    public void close() {
        image.close();
        image = null;
        texture = null;
        textureManager.release(uniqueLocation);
    }

    public static ImageRendererFactory fromPath(Path imagePath, Identifier location, boolean textureFiltering) {
        return (ImageRendererFactory.OnThread) () -> () -> new DynamicTextureImage(NativeImage.read(new FileInputStream(imagePath.toFile())), location, textureFiltering);
    }
}
