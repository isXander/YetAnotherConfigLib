package dev.isxander.yacl3.gui.render;

import net.minecraft.client.gui.GuiGraphics;

//? if >=1.21.6 {
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.navigation.ScreenRectangle;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
*///?}

public interface GuiRenderStateSink {
    //? if >=1.21.6 {
    void yacl$submit(GuiElementRenderState renderState);

    static void submit(GuiGraphics graphics, GuiElementRenderState renderState) {
        ((GuiRenderStateSink) graphics).yacl$submit(renderState);
    }

    ScreenRectangle yacl$peekScissorStack();

    static ScreenRectangle peekScissorStack(GuiGraphics graphics) {
        return ((GuiRenderStateSink) graphics).yacl$peekScissorStack();
    }
    //?} else {
    /*MultiBufferSource yacl$bufferSource();

    static MultiBufferSource bufferSource(GuiGraphics graphics) {
        return ((GuiRenderStateSink) graphics).yacl$bufferSource();
    }
    *///?}
}
