package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.isxander.yacl3.api.utils.Dimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.joml.Matrix4f;

import java.awt.*;

public abstract class AbstractWidget implements GuiEventListener, Renderable, NarratableEntry {
    private static final WidgetSprites SPRITES = new WidgetSprites(
            new ResourceLocation("widget/button"), // normal
            new ResourceLocation("widget/button_disabled"), // disabled & !focused
            new ResourceLocation("widget/button_highlighted"), // !disabled & focused
            new ResourceLocation("widget/slider_highlighted") // disabled & focused
    );

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

        graphics.blitSprite(SPRITES.get(enabled, hovered), x1, y1, width, height);
    }

    protected void drawOutline(GuiGraphics graphics, int x1, int y1, int x2, int y2, int width, int color) {
        graphics.fill(x1, y1, x2, y1 + width, color);
        graphics.fill(x2, y1, x2 - width, y2, color);
        graphics.fill(x1, y2, x2, y2 - width, color);
        graphics.fill(x1, y1, x1 + width, y2, color);
    }

    protected void fillSidewaysGradient(GuiGraphics graphics, int x1, int y1, int x2, int y2, int z, int startColor, int endColor) {
        //Fills a gradient, left to right
        //Uses practically the same method as the GuiGraphics class, but with the x/y moved
        //Has a custom "z" value in case needed for later
        VertexConsumer vertex = graphics.bufferSource().getBuffer(RenderType.gui());
        Matrix4f matrix4f = graphics.pose().last().pose();
        vertex.vertex(matrix4f, x2, y2, z).color(startColor).endVertex();
        vertex.vertex(matrix4f, x2, y1, z).color(startColor).endVertex();
        vertex.vertex(matrix4f, x1, y1, z).color(endColor).endVertex();
        vertex.vertex(matrix4f, x1, y2, z).color(endColor).endVertex();
    }


    protected void drawRainbowGradient(GuiGraphics graphics, int x1, int y1, int x2, int y2, int z) {
        //Draws a rainbow gradient, left to right
        int red = Color.red.getRGB();
        int yellow = Color.yellow.getRGB();
        int green = Color.green.getRGB();
        int cyan = Color.cyan.getRGB();
        int blue = Color.blue.getRGB();
        int purple = Color.magenta.getRGB();
        int x = x2 - x1;
        int i = 6;
        //TODO - Some int array goofy stuff to reduce code

        fillSidewaysGradient(graphics, x1 + (x / i), y1, x1, y2, z, red, yellow);
        fillSidewaysGradient(graphics, x1 + (x / i * 2), y1, x1 + (x / i), y2, z, yellow, green);
        fillSidewaysGradient(graphics, x1 + (x / i * 3), y1, x1 + (x / i * 2), y2, z, green, cyan);
        fillSidewaysGradient(graphics, x1 + (x / i * 4), y1, x1 + (x / i * 3), y2, z, cyan, blue);
        fillSidewaysGradient(graphics, x1 + (x / i * 5), y1, x1 + (x / i * 4), y2, z, blue, purple);
        fillSidewaysGradient(graphics, x1 + (x / i * 6), y1, x1 + (x / i * 5), y2, z, purple, red);
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
}
