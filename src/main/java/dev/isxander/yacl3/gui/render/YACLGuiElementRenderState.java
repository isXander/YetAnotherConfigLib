package dev.isxander.yacl3.gui.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//? if >=1.21.6 {
/*import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import com.mojang.blaze3d.pipeline.RenderPipeline;

*///?}

public interface YACLGuiElementRenderState /*? if >=1.21.6 {*//*extends GuiElementRenderState *//*?}*/ {

    BaseRenderState baseState();

    //? if >=1.21.6 {
    /*@Override
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
    *///?} else {
    void buildVertices(VertexConsumer vertexConsumer, float z);
    //?}

    default VertexConsumer add2DVertex(
            VertexConsumer vertexConsumer,
            float x, float y, float z
    ) {
        //? if >=1.21.6 {
        /*return vertexConsumer.addVertexWith2DPose(this.baseState().pose(), x, y, z);
        *///?} else {
        return vertexConsumer.addVertex(this.baseState().pose(), x, y, z);
        //?}
    }

    default void submit(GuiGraphics graphics) {
        //? if >=1.21.6 {
        /*GuiRenderStateSink.submit(graphics, this);
        *///?} else {
        // TODO: don't drawSpecial as it finishes the batch
        VertexConsumer vertexConsumer = GuiRenderStateSink.bufferSource(graphics).getBuffer(baseState().renderType());
        buildVertices(vertexConsumer, 0);
        //?}
    }


}
