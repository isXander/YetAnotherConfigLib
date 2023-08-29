package dev.isxander.yacl3.gui.image.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererFactory;
import net.minecraft.client.gui.GuiComponent;
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
    public int render(PoseStack pose, int x, int y, int renderWidth, float tickDelta) {
        float ratio = renderWidth / (float)this.width;
        int targetHeight = (int) (this.height * ratio);

        RenderSystem.setShaderTexture(0, location);

        pose.pushPose();
        pose.translate(x, y, 0);
        pose.scale(ratio, ratio, 1);
        GuiComponent.blit(pose, 0, 0, this.u, this.v, this.width, this.height, this.textureWidth, this.textureHeight);
        pose.popPose();

        return targetHeight;
    }

    @Override
    public void close() {

    }

    public static ImageRendererFactory<ResourceTextureImage> createFactory(ResourceLocation location, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        return (ImageRendererFactory.OnThread<ResourceTextureImage>) () -> () -> new ResourceTextureImage(location, u, v, width, height, textureWidth, textureHeight);
    }
}
