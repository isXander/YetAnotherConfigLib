package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ElementListWidgetExt<E extends ElementListWidgetExt.Entry<E>> extends ContainerObjectSelectionList<E> implements LayoutElement {
    private double smoothScrollAmount = getScrollAmount();
    private boolean returnSmoothAmount = false;
    private final boolean doSmoothScrolling;

    public ElementListWidgetExt(Minecraft client, int x, int y, int width, int height, boolean smoothScrolling) {
        super(client, x, y, width, height);
        this.doSmoothScrolling = smoothScrolling;
        setRenderBackground(true);
        setRenderHeader(false, 0);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        // default implementation bases scroll step from total height of entries, this is constant
        this.setScrollAmount(this.getScrollAmount() - (vertical + horizontal) * 20);
        return true;
    }

    @Override
    protected int getScrollbarPosition() {
        // default implementation does not respect left/right
        return this.getX() + this.getWidth() - 2;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        smoothScrollAmount = Mth.lerp(Minecraft.getInstance().getDeltaFrameTime() * 0.5, smoothScrollAmount, getScrollAmount());
        returnSmoothAmount = true;

        graphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());

        super.renderWidget(graphics, mouseX, mouseY, delta);

        graphics.disableScissor();

        returnSmoothAmount = false;
    }

    public void updateDimensions(ScreenRectangle rectangle) {
        this.setX(rectangle.left());
        this.setY(rectangle.top());
        this.setWidth(rectangle.width());
        this.setHeight(rectangle.height());
    }

    /**
     * awful code to only use smooth scroll state when rendering,
     * not other code that needs target scroll amount
     */
    @Override
    public double getScrollAmount() {
        if (returnSmoothAmount && doSmoothScrolling)
            return smoothScrollAmount;

        return super.getScrollAmount();
    }

    protected void resetSmoothScrolling() {
        this.smoothScrollAmount = getScrollAmount();
    }

    @Nullable
    @Override
    protected E getEntryAtPosition(double x, double y) {
        y += getScrollAmount();

        if (x < this.getX() || x > this.getX() + this.getWidth())
            return null;

        int currentY = this.getY() - headerHeight + 4;
        for (E entry : children()) {
            if (y >= currentY && y <= currentY + entry.getItemHeight()) {
                return entry;
            }

            currentY += entry.getItemHeight();
        }

        return null;
    }

    /*
      below code is licensed from cloth-config under LGPL3
      modified to inherit vanilla's EntryListWidget and use yarn mappings

      code is responsible for having dynamic item heights
    */

    @Override
    protected int getMaxPosition() {
        return children().stream().map(E::getItemHeight).reduce(0, Integer::sum) + headerHeight;
    }

    @Override
    protected void centerScrollOn(E entry) {
        double d = (this.height) / -2d;
        for (int i = 0; i < this.children().indexOf(entry) && i < this.getItemCount(); i++)
            d += children().get(i).getItemHeight();
        this.setScrollAmount(d);
    }

    @Override
    protected int getRowTop(int index) {
        int integer = getY() + 4 - (int) this.getScrollAmount() + headerHeight;
        for (int i = 0; i < children().size() && i < index; i++)
            integer += children().get(i).getItemHeight();
        return integer;
    }

    @Override
    protected void renderList(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int left = this.getRowLeft();
        int right = this.getRowWidth();
        int count = this.getItemCount();

        for(int i = 0; i < count; ++i) {
            E entry = children().get(i);
            int top = this.getRowTop(i);
            int bottom = top + entry.getItemHeight();
            int entryHeight = entry.getItemHeight() - 4;
            if (bottom >= this.getY() && top <= this.getY() + this.getHeight()) {
                this.renderItem(graphics, mouseX, mouseY, delta, i, left, top, right, entryHeight);
            }
        }
    }

    /* END cloth config code */

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
    }

    public abstract static class Entry<E extends Entry<E>> extends ContainerObjectSelectionList.Entry<E> {
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (GuiEventListener child : this.children()) {
                if (child.mouseClicked(mouseX, mouseY, button)) {
                    if (button == InputConstants.MOUSE_BUTTON_LEFT)
                        this.setDragging(true);
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (isDragging() && button == InputConstants.MOUSE_BUTTON_LEFT) {
                for (GuiEventListener child : this.children()) {
                    if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
                        return true;
                }
            }
            return false;
        }

        public int getItemHeight() {
            return 22;
        }
    }
}
