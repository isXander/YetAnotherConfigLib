package dev.isxander.yacl3.gui.image;

import com.mojang.blaze3d.vertex.PoseStack;

public interface ImageRenderer {
    int render(PoseStack graphics, int x, int y, int renderWidth, float tickDelta);

    void close();

    default void tick() {}
}
