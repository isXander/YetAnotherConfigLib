package dev.isxander.yacl3.gui.image.impl;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.io.FileInputStream;
import java.nio.file.Path;

public class DynamicTextureImage implements ImageRenderer {
    protected static final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

    protected NativeImage image;
    protected DynamicTexture texture;
    protected final ResourceLocation uniqueLocation;
    protected final int width, height;

    public DynamicTextureImage(NativeImage image, ResourceLocation location) {
        RenderSystem.assertOnRenderThread();

        this.image = image;
        this.texture = new DynamicTexture(image);
        this.uniqueLocation = location;
        textureManager.register(this.uniqueLocation, this.texture);
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    @Override
    public int render(PoseStack pose, int x, int y, int renderWidth, float tickDelta) {
        if (image == null) return 0;

        float ratio = renderWidth / (float)this.width;
        int targetHeight = (int) (this.height * ratio);

        RenderSystem.setShaderTexture(0, uniqueLocation);

        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(ratio, ratio, 1);

        GuiComponent.blit(pose, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        pose.popPose();

        return targetHeight;
    }

    @Override
    public void close() {
        image.close();
        image = null;
        texture = null;
        textureManager.release(uniqueLocation);
    }

    public static ImageRendererFactory<DynamicTextureImage> fromPath(Path imagePath, ResourceLocation location) {
        return (ImageRendererFactory.OnThread<DynamicTextureImage>) () -> () -> new DynamicTextureImage(NativeImage.read(new FileInputStream(imagePath.toFile())), location);
    }
}
