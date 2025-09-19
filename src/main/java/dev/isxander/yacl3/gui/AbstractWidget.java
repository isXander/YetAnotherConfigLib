package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.render.ColorGradientRenderState;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.gui.utils.YACLRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import org.joml.Matrix4f;

import java.awt.Color;

//? if >=1.21.9 {
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}

public abstract class AbstractWidget implements GuiEventListener, Renderable, NarratableEntry {
    protected final Minecraft client = Minecraft.getInstance();
    protected final Font textRenderer = client.font;
    protected final int inactiveColor = 0xFFA0A0A0;

    private Dimension<Integer> dim;

    public AbstractWidget(Dimension<Integer> dim) {
        this.dim = dim;
    }

    public boolean canReset() {
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (dim == null) return false;
        return this.dim.isPointInside((int) mouseX, (int) mouseY);
    }

    public void setDimension(Dimension<Integer> dim) {
        this.dim = dim;
    }

    public Dimension<Integer> getDimension() {
        return dim;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    public void unfocus() {

    }

    public boolean matchesSearch(String query) {
        return true;
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {

    }

    protected void drawButtonRect(GuiGraphics graphics, int x1, int y1, int x2, int y2, boolean hovered, boolean enabled) {
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

        YACLRenderHelper.renderButtonTexture(graphics, x1, y1, width, height, enabled, hovered);
    }

    protected void drawOutline(GuiGraphics graphics, int x1, int y1, int x2, int y2, int width, int color) {
        graphics.fill(x1, y1, x2, y1 + width, color);
        graphics.fill(x2, y1, x2 - width, y2, color);
        graphics.fill(x1, y2, x2, y2 - width, color);
        graphics.fill(x1, y1, x1 + width, y2, color);
    }

    protected void drawRainbowGradient(GuiGraphics graphics, int x1, int y1, int x2, int y2) {
        //Draws a rainbow gradient, left to right
        int[] colors = new int[] {0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF, 0xFFFF00FF, 0xFFFF0000}; //all the colors in the gradient
        int width = x2 - x1;
        int maxColors = colors.length - 1;

        for (int color = 0; color < maxColors; color++) {
            //First checks if the final color is being rendered, if true -> uses x2 int instead of x1
            //if false -> it adds the width divided by the max colors multiplied by the current color plus one to the x1 int
            //the x2 int for the fillSidewaysGradient is the same formula, excluding the additional plus one.
            //The gradient colors is determined by the color int and the color int plus one, which is why red is in the colors array twice
            ColorGradientRenderState.createHorizontal(
                    graphics,
                    x1 + (width / maxColors * color), y1,
                    color == maxColors - 1 ? x2 : x1 + (width / maxColors * (color + 1)), y2,
                    colors[color], colors[color + 1]
            ).submit(graphics);
        }

    }

    protected int multiplyColor(int hex, float amount) {
        Color color = new Color(hex, true);

        return new Color(Math.max((int)(color.getRed() * amount), 0),
                  Math.max((int)(color.getGreen() * amount), 0),
                  Math.max((int)(color.getBlue() * amount), 0),
                  color.getAlpha()).getRGB();
    }

    public void playDownSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    //? if >=1.21.9 {
    @Override
    public final boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        return this.onMouseClicked(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
    }
    //?} else {
    /*@Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.onMouseClicked(mouseX, mouseY, button);
    }
    *///?}
    public boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    //? if >=1.21.9 {
    @Override
    public final boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        return this.onMouseReleased(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
    }
    //?} else {
    /*@Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.onMouseReleased(mouseX, mouseY, button);
    }
    *///?}
    public boolean onMouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    //? if >=1.21.9 {
    @Override
    public final boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dx, double dy) {
        return this.onMouseDragged(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button(), dx, dy);
    }
    //?} else {
    /*@Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        return this.onMouseDragged(mouseX, mouseY, button, dx, dy);
    }
    *///?}
    public boolean onMouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        return false;
    }

    //? if >=1.21.9 {
    @Override
    public final boolean keyPressed(KeyEvent keyEvent) {
        return this.onKeyPressed(keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers());
    }
    //?} else {
    /*@Override
    public final boolean keyPressed(int keycode, int scancode, int modifiers) {
        return this.onKeyPressed(keycode, scancode, modifiers);
    }
    *///?}
    public boolean onKeyPressed(int key, int scancode, int modifiers) {
        return false;
    }

    //? if >=1.21.9 {
    @Override
    public final boolean keyReleased(KeyEvent keyEvent) {
        return this.onKeyReleased(keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers());
    }
    //?} else {
    /*@Override
    public final boolean keyReleased(int keycode, int scancode, int modifiers) {
        return this.onKeyReleased(keycode, scancode, modifiers);
    }
    *///?}
    public boolean onKeyReleased(int key, int scancode, int modifiers) {
        return false;
    }

    //? if >=1.21.9 {
    @Override
    public final boolean charTyped(CharacterEvent characterEvent) {
        return this.onCharTyped((char) characterEvent.codepoint(), characterEvent.codepointAsString(), characterEvent.modifiers());
    }
    //?} else {
    /*@Override
    public final boolean charTyped(char codePoint, int modifiers) {
        return this.onCharTyped(codePoint, Character.toString(codePoint), modifiers);
    }
    *///?}
    public boolean onCharTyped(char ch, String cpStr, int modifiers) {
        return false;
    }

}
