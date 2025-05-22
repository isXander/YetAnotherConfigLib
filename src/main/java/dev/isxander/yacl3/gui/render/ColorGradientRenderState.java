package dev.isxander.yacl3.gui.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;

public record ColorGradientRenderState(
        BaseRenderState baseState,
        int x0,
        int y0,
        int x1,
        int y1,
        int x0y0Color,
        int x1y0Color,
        int x0y1Color,
        int x1y1Color
) implements YACLGuiElementRenderState {
    @Override
    public void buildVertices(VertexConsumer vertexConsumer, float z) {
        add2DVertex(vertexConsumer, this.x0(), this.y0(), z).setColor(this.x0y0Color());
        add2DVertex(vertexConsumer, this.x0(), this.y1(), z).setColor(this.x0y1Color());
        add2DVertex(vertexConsumer, this.x1(), this.y1(), z).setColor(this.x1y1Color());
        add2DVertex(vertexConsumer, this.x1(), this.y0(), z).setColor(this.x1y0Color());
    }

    public static ColorGradientRenderState create(
            GuiGraphics graphics,
            int x0, int y0,
            int x1, int y1,
            int x0y0Color, int x1y0Color,
            int x0y1Color, int x1y1Color
    ) {
        return new ColorGradientRenderState(
                BaseRenderState.create(graphics, null, x0, y0, x1, y1),
                x0, y0, x1, y1,
                x0y0Color, x1y0Color,
                x0y1Color, x1y1Color
        );
    }

    public static ColorGradientRenderState createHorizontal(
            GuiGraphics graphics,
            int x0, int y0,
            int x1, int y1,
            int leftColor, int rightColor
    ) {
        return create(
                graphics,
                x0, y0, x1, y1,
                leftColor, rightColor,
                leftColor, rightColor
        );
    }

    public static ColorGradientRenderState createVertical(
            GuiGraphics graphics,
            int x0, int y0,
            int x1, int y1,
            int topColor, int bottomColor
    ) {
        return create(
                graphics,
                x0, y0, x1, y1,
                topColor, topColor,
                bottomColor, bottomColor
        );
    }
}
