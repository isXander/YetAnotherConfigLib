package dev.isxander.yacl3.mixin;

import dev.isxander.yacl3.gui.render.GuiRenderStateSink;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin implements GuiRenderStateSink {

    @Shadow @Final
    public GuiRenderState guiRenderState;

    @Override
    public void yacl$submit(GuiElementRenderState renderState) {
        this.guiRenderState.addGuiElement(renderState);
    }


    @Shadow @Final private GuiGraphicsExtractor.ScissorStack scissorStack;

    @Override
    public ScreenRectangle yacl$peekScissorStack() {
        return this.scissorStack.peek();
    }
}
