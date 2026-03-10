package dev.isxander.yacl3.gui.image.impl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.debug.DebugProperties;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererFactory;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.function.Supplier;

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
        this.texture = new DynamicTexture(/*? if >=1.21.11 >>*/ /*location::toString,*/ image);
        // TODO 1.21.11
        //? if <1.21.11
        //this.texture.setFilter(textureFiltering, false);
        this.textureFiltering = textureFiltering;
        this.uniqueLocation = location;
        textureManager.register(this.uniqueLocation, this.texture);
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    @Override
    public int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta) {
        if (image == null) return 0;

        float ratio = renderWidth / (float)this.width;
        int targetHeight = (int) (this.height * ratio);

        GuiUtils.pushPose(graphics);
        GuiUtils.translate2D(graphics, x, y);
        GuiUtils.scale2D(graphics, ratio, ratio);

        GuiUtils.blitGuiTex(
                graphics,
                uniqueLocation,
                0, 0,
                0, 0,
                this.width, this.height,
                this.width, this.height,
                textureFiltering
        );

        GuiUtils.popPose(graphics);

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
