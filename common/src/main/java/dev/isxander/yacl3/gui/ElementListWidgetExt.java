package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ElementListWidgetExt<E extends ElementListWidgetExt.Entry<E>> extends ContainerObjectSelectionList<E> implements LayoutElement {
    protected int x, y;

    private double smoothScrollAmount = getScrollAmount();
    private boolean returnSmoothAmount = false;
    private final boolean doSmoothScrolling;

    public ElementListWidgetExt(Minecraft client, int x, int y, int width, int height, boolean smoothScrolling) {
        super(client, width, height, y, y + height, 22);
        this.x = this.x0 = x;
        this.y = y;
        this.x1 = this.x0 + width;
        this.doSmoothScrolling = smoothScrolling;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // default implementation bases scroll step from total height of entries, this is constant
        this.setScrollAmount(this.getScrollAmount() - amount * 20);
        return true;
    }

    @Override
    protected void renderBackground(PoseStack matrices) {
        // render transparent background if in-game.
        setRenderBackground(true);
        setRenderTopAndBottom(false);
    }

    @Override
    protected int getScrollbarPosition() {
        // default implementation does not respect left/right
        return this.x1 - 2;
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        smoothScrollAmount = Mth.lerp(Minecraft.getInstance().getDeltaFrameTime() * 0.5, smoothScrollAmount, getScrollAmount());
        returnSmoothAmount = true;

        GuiComponent.enableScissor(x0, y0, x1, y1);

        super.render(matrices, mouseX, mouseY, delta);

        GuiComponent.disableScissor();

        returnSmoothAmount = false;
    }

    public void updateDimensions(ScreenRectangle rectangle) {
        this.x0 = rectangle.left();
        this.y0 = rectangle.top();
        this.x1 = rectangle.right();
        this.y1 = rectangle.bottom();
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

        if (x < this.x0 || x > this.x1)
            return null;

        int currentY = this.y0 - headerHeight + 4;
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
        int integer = y0 + 4 - (int) this.getScrollAmount() + headerHeight;
        for (int i = 0; i < children().size() && i < index; i++)
            integer += children().get(i).getItemHeight();
        return integer;
    }

    @Override
    protected void renderList(PoseStack matrices, int mouseX, int mouseY, float delta) {
        int left = this.getRowLeft();
        int right = this.getRowWidth();
        int count = this.getItemCount();

        for(int i = 0; i < count; ++i) {
            E entry = children().get(i);
            int top = this.getRowTop(i);
            int bottom = top + entry.getItemHeight();
            int entryHeight = entry.getItemHeight() - 4;
            if (bottom >= this.y0 && top <= this.y1) {
                this.renderItem(matrices, mouseX, mouseY, delta, i, left, top, right, entryHeight);
            }
        }
    }

    /* END cloth config code */

    @Override
    public void setX(int i) {
        this.x = x0 = i;
        this.x1 = x0 + width;
    }

    @Override
    public void setY(int i) {
        this.y = y0 = i;
        this.y1 = y0 + height;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void visitWidgets(Consumer<net.minecraft.client.gui.components.AbstractWidget> widget) {

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
