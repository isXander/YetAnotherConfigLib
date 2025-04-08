package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.mixin.AbstractSelectionListAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class ElementListWidgetExt<E extends ElementListWidgetExt.Entry<E>> extends ContainerObjectSelectionList<E> implements LayoutElement {
    protected static final int SCROLLBAR_WIDTH = 6;

    private double smoothScrollAmount = scrollAmount();
    private boolean returnSmoothAmount = false;
    private final boolean doSmoothScrolling;
    private boolean usingScrollbar;

    public ElementListWidgetExt(Minecraft client, int x, int y, int width, int height, boolean smoothScrolling) {
        //? if >=1.21.4 {
        super(client, width, x, y, height, 0);
        ((AbstractSelectionListAccessor) this).setRenderHeader(false);
        //?} else {
        /*super(client, width, x, y, height);
        *///?}
        this.doSmoothScrolling = smoothScrolling;

        //? if <1.21.4
        /*setRenderHeader(false, 0);*/
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        double scroll = vertical + horizontal;

        // default implementation bases scroll step from total height of entries, this is constant
        this.setScrollAmount(this.scrollAmount() - scroll * 20);
        return true;
    }

    //? if <1.21.4 {
    /*protected int scrollBarX() {
        return this.getScrollbarPosition();
    }
    @Override
    protected int getScrollbarPosition() {
    *///?} else {
    @Override
    protected int scrollBarX() {
    //?}
        // default implementation does not respect left/right
        return this.getX() + this.getWidth() - SCROLLBAR_WIDTH;
    }

    //? if >=1.21.4 {
    @Override
    protected int scrollBarY() {
        return Math.max(this.getY(), (int)scrollAmount() * (this.height - this.scrollerHeight()) / this.maxScrollAmount() + this.getY());
    }
    //?} else {
    /*protected int maxScrollAmount() {
        return this.getMaxPosition();
    }
    *///?}

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (usingScrollbar) {
            resetSmoothScrolling();
        }

        smoothScrollAmount = Mth.lerp(
                delta * 0.5,
                smoothScrollAmount,
                scrollAmount()
        );
        if (Math.abs(smoothScrollAmount - scrollAmount()) < 1e-5) {
            smoothScrollAmount = scrollAmount();
        }
        returnSmoothAmount = true;


        graphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());

        super.renderWidget(graphics, mouseX, mouseY, delta);

        graphics.disableScissor();

        returnSmoothAmount = false;
    }

    /*? if <1.21.4 {*/
    /*@Override
    *//*?}*/
    protected boolean isValidMouseClick(int button) {
        return button == InputConstants.MOUSE_BUTTON_LEFT || button == InputConstants.MOUSE_BUTTON_RIGHT || button == InputConstants.MOUSE_BUTTON_MIDDLE;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= scrollBarX() && mouseX < scrollBarX() + SCROLLBAR_WIDTH) {
            usingScrollbar = true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            usingScrollbar = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
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
    //? if <1.21.4 {
    /*// backwards compatible method to reduce stonecutter comments
    protected double scrollAmount() {
        return this.getScrollAmount();
    }

    @Override
    public double getScrollAmount() {
    *///?} else {
    @Override
    public double scrollAmount() {
    //?}
        if (returnSmoothAmount && doSmoothScrolling)
            return smoothScrollAmount;

        //? if >=1.21.4 {
        return super.scrollAmount();
        //?} else {
        /*return super.getScrollAmount();
        *///?}
    }

    protected void resetSmoothScrolling() {
        this.smoothScrollAmount = /*? if >=1.21.4 {*/super.scrollAmount()/*?} else {*//*super.getScrollAmount()*//*?}*/;
    }

    @Nullable
    @Override
    protected E getEntryAtPosition(double x, double y) {
        y += scrollAmount();

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

    //? if <1.21.4 {
    /*protected int contentHeight() {
        return this.getMaxPosition();
    }
    @Override
    protected int getMaxPosition() {
    *///?} else {
    @Override
    protected int contentHeight() {
    //?}
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
    /*? if >=1.21.2 {*/ public /*?} else {*/ /*protected *//*?}*/
    int getRowTop(int index) {
        int integer = getY() + 4 - (int) this.scrollAmount() + headerHeight;
        for (int i = 0; i < children().size() && i < index; i++)
            integer += children().get(i).getItemHeight();
        return integer;
    }

    @Override
    protected void ensureVisible(E entry) {
        int entryIndex = this.children().indexOf(entry);

        int top = this.getRowTop(entryIndex);
        int j = top - this.getY() - 4 - entry.getItemHeight();
        if (j < 0) {
            this.setScrollAmount(this.scrollAmount() + j);
        }

        int k = this.getY() + this.getHeight()  - top - entry.getItemHeight() * 2;
        if (k < 0) {
            this.setScrollAmount(this.scrollAmount() - k);
        }
    }

    @Override
    protected void renderListItems(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
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
                    if (button == InputConstants.MOUSE_BUTTON_LEFT || button == InputConstants.MOUSE_BUTTON_RIGHT)
                        this.setDragging(true);
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (isDragging() && (button == InputConstants.MOUSE_BUTTON_LEFT || button == InputConstants.MOUSE_BUTTON_RIGHT)) {
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
