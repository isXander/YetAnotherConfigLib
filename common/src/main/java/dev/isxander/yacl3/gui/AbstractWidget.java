package dev.isxander.yacl3.gui;

import dev.isxander.yacl3.api.utils.Dimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;

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

        int i = !enabled ? 0 : hovered ? 2 : 1;
        graphics.blit(net.minecraft.client.gui.components.AbstractWidget.WIDGETS_LOCATION, x1, y1, 0, 0, 46 + i * 20, width / 2, height, 256, 256);
        graphics.blit(net.minecraft.client.gui.components.AbstractWidget.WIDGETS_LOCATION, x1 + width / 2, y1, 0, 200 - width / 2f, 46 + i * 20, width / 2, height, 256, 256);

        if (hovered && !enabled) {
            drawOutline(graphics, x1, y1, x2, y2, 1, 0xFFD0D0D0);
        }
    }

    protected void drawOutline(GuiGraphics graphics, int x1, int y1, int x2, int y2, int width, int color) {
        graphics.fill(x1, y1, x2, y1 + width, color);
        graphics.fill(x2, y1, x2 - width, y2, color);
        graphics.fill(x1, y2, x2, y2 - width, color);
        graphics.fill(x1, y1, x1 + width, y2, color);
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
