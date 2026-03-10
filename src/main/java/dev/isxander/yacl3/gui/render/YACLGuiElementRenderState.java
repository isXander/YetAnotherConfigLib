package dev.isxander.yacl3.gui.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface YACLGuiElementRenderState extends GuiElementRenderState {

    BaseRenderState baseState();

    @Override
    default void buildVertices(VertexConsumer vertexConsumer) {
        this.buildVertices(vertexConsumer, 0);
    }

    // purely to ease development, we backport this method from <1.21.9 and have a placeholder Z
    void buildVertices(VertexConsumer vertexConsumer, float z);

    @Override
    default @NotNull RenderPipeline pipeline() {
        return baseState().pipeline();
    }

    @Override
    default @NotNull TextureSetup textureSetup() {
        return baseState().textureSetup();
    }

    @Override
    default @Nullable ScreenRectangle scissorArea() {
        return baseState().scissorArea();
    }

    @Override
    default @Nullable ScreenRectangle bounds() {
        return baseState().bounds();
    }

    default VertexConsumer add2DVertex(
            VertexConsumer vertexConsumer,
            float x, float y, float z
    ) {
        return vertexConsumer.addVertexWith2DPose(this.baseState().pose(), x, y);
    }

    default void submit(GuiGraphicsExtractor graphics) {
        GuiRenderStateSink.submit(graphics, this);
    }


}
