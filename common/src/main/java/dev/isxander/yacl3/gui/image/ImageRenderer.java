package dev.isxander.yacl3.gui.image;

import net.minecraft.client.gui.GuiGraphics;

public interface ImageRenderer {
    int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta);

    void close();

    default void tick() {}
}
