package dev.isxander.yacl3.gui.image;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface ImageRenderer {
    int render(GuiGraphicsExtractor graphics, int x, int y, int renderWidth, float tickDelta);

    void close();

    default void tick() {}
}
