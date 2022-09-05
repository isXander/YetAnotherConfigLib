package dev.isxander.yacl.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl.api.utils.Dimension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

public abstract class AbstractWidget implements Element, Drawable, Selectable {
    protected final MinecraftClient client = MinecraftClient.getInstance();
    protected final TextRenderer textRenderer = client.textRenderer;

    protected Dimension<Integer> dim;

    public AbstractWidget(Dimension<Integer> dim) {
        this.dim = dim;
    }

    public void setDimension(Dimension<Integer> dim) {
        this.dim = dim;
    }

    public Dimension<Integer> getDimension() {
        return dim;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public void unfocus() {

    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    protected void drawButtonRect(MatrixStack matrices, int x1, int y1, int x2, int y2, boolean hovered) {
        if (x1 > x2) {
            int xx1 = x1;
            x1 = x2;
            x2 = xx1;
        }
        if (y1 > y2) {
            int yy1 = y1;
            y1 = y2;
            y2 = yy1;
        }
        int width = x2 - x1;
        int height = y2 - y1;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ClickableWidget.WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = hovered ? 2 : 1;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        DrawableHelper.drawTexture(matrices, x1, y1, 0, 0, 46 + i * 20, width / 2, height, 256, 256);
        DrawableHelper.drawTexture(matrices, x1 + width / 2, y1, 0, 200 - width / 2f, 46 + i * 20, width / 2, height, 256, 256);
    }

    public void playDownSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
