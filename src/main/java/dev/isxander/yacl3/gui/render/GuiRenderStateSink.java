package dev.isxander.yacl3.gui.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface GuiRenderStateSink {
    void yacl$submit(GuiElementRenderState renderState);

    static void submit(GuiGraphicsExtractor graphics, GuiElementRenderState renderState) {
        ((GuiRenderStateSink) graphics).yacl$submit(renderState);
    }

    ScreenRectangle yacl$peekScissorStack();

    static ScreenRectangle peekScissorStack(GuiGraphicsExtractor graphics) {
        return ((GuiRenderStateSink) graphics).yacl$peekScissorStack();
    }
}
