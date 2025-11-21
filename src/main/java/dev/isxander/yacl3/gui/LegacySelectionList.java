//? if <1.21.9 {
/*package dev.isxander.yacl3.gui;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.mixin.AbstractSelectionListAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class LegacySelectionList<E extends LegacySelectionList.Entry<E>> extends ContainerObjectSelectionList<E> implements LayoutElement {

    public LegacySelectionList(Minecraft client, int y, int width, int height) {
        //? if >=1.21.4 {
        super(client, width, height, y, 0);
        ((AbstractSelectionListAccessor) this).setRenderHeader(false);
        //?} else {
        /^super(client, width, height, y, 20);
         ^///?}

        //? if <1.21.4
        /^setRenderHeader(false, 0);^/
    }

    // for parity with modern, no need in legacy as everything is positioned on render
    protected void repositionEntries() {}

    /^
    The default implementation of scrollbarX does not respect left/right positioning of the list.
    ^/
    //? if <1.21.4 {
    /^@Override
    protected int getScrollbarPosition() {
        return this.scrollBarX();
    }
    ^///?} else {
    @Override
    //?}
    protected int scrollBarX() {
        return this.getX() + this.getWidth() - SCROLLBAR_WIDTH;
    }

    /^
    The legacy list has a fixed entry height for elements.
    This implementation allows each element to define its own height.
    So we override the relevant methods to use this behaviour instead.
     ^/
    //? <1.21.4 {
    /^@Override
    protected int getMaxPosition() {
        return this.contentHeight();
    }
    ^///?} else {
    @Override
    //?}
    protected int contentHeight() {
        return children().stream().mapToInt(E::getHeight).sum();
    }

    @Override
    protected void centerScrollOn(E entry) {
        double d = (this.height) / -2d;
        for (int i = 0; i < this.children().indexOf(entry) && i < this.getItemCount(); i++)
            d += children().get(i).getHeight();
        this.setScrollAmount(d);
    }

    @Override
    /^? if >=1.21.2 {^/ public /^?} else {^/ /^protected ^//^?}^/
    int getRowTop(int index) {
        int integer = getY() + 4 - (int) this.scrollAmount() + headerHeight;
        for (int i = 0; i < children().size() && i < index; i++)
            integer += children().get(i).getHeight();
        return integer;
    }

    @Override
    protected void ensureVisible(E entry) {
        int entryIndex = this.children().indexOf(entry);

        int top = this.getRowTop(entryIndex);
        int j = top - this.getY() - 4 - entry.getHeight();
        if (j < 0) {
            this.setScrollAmount(this.scrollAmount() + j);
        }

        int k = this.getY() + this.getHeight() - top - entry.getHeight() * 2;
        if (k < 0) {
            this.setScrollAmount(this.scrollAmount() - k);
        }
    }

    @Nullable
    @Override
    protected E getEntryAtPosition(double x, double y) {
        y += scrollAmount();

        if (x < this.getX() || x > this.getX() + this.getWidth())
            return null;

        int currentY = this.getY() - headerHeight + 4;
        for (E entry : children()) {
            if (y >= currentY && y <= currentY + entry.getHeight()) {
                return entry;
            }

            currentY += entry.getHeight();
        }

        return null;
    }

    @Override
    protected void renderListItems(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int left = this.getRowLeft();
        int right = this.getRowWidth();
        int count = this.getItemCount();

        for(int i = 0; i < count; ++i) {
            E entry = children().get(i);
            int top = this.getRowTop(i);
            int bottom = top + entry.getHeight();
            int entryHeight = entry.getHeight() - 4;
            if (bottom >= this.getY() && top <= this.getY() + this.getHeight()) {
                this.renderItem(graphics, mouseX, mouseY, delta, i, left, top, right, entryHeight);
            }
        }
    }

    //? if <1.21.4 {
    /^public double scrollAmount() {
        return this.getScrollAmount();
    }
    ^///?}


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.setScrollAmount(this.scrollAmount() - scrollY * 20);
        return true;
    }

    public abstract static class Entry<E extends LegacySelectionList.Entry<E>> extends ContainerObjectSelectionList.Entry<E> implements LayoutElement {
        public static final int CONTENT_PADDING = 2;
        private int x, y, width, height;
        private boolean hovered;
        private final LegacySelectionList<E> parent;

        public Entry(LegacySelectionList<E> parent) {
            this.parent = parent;
        }

        public abstract void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float deltaTicks);

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
            this.setX(left);
            this.setY(top);
            this.setWidth(width);
            this.hovered = hovering;
            // don't set height as the entry itself controls that

            this.renderContent(guiGraphics, mouseX, mouseY, hovering, partialTick);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.getRectangle().containsPoint((int) mouseX, (int) mouseY);
        }

        @Override
        public boolean isFocused() {
            return parent.getFocused() == this;
        }

        public boolean isHovered() {
            return hovered;
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
        public void setX(int x) {
            this.x = x;
        }

        @Override
        public void setY(int y) {
            this.y = y;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getContentX() {
            return this.getX() + CONTENT_PADDING;
        }

        public int getContentY() {
            return this.getY() + CONTENT_PADDING;
        }

        public int getContentHeight() {
            return this.getHeight() - CONTENT_PADDING * 2;
        }

        public int getContentYMiddle() {
            return this.getContentY() + this.getContentHeight() / CONTENT_PADDING;
        }

        public int getContentBottom() {
            return this.getContentY() + this.getContentHeight();
        }

        public int getContentWidth() {
            return this.getWidth() - CONTENT_PADDING * 2;
        }

        public int getContentXMiddle() {
            return this.getContentX() + this.getContentWidth() / CONTENT_PADDING;
        }

        public int getContentRight() {
            return this.getContentX() + this.getContentWidth();
        }

        @Override
        public void visitWidgets(Consumer<AbstractWidget> consumer) {

        }

        @Override
        public @NotNull ScreenRectangle getRectangle() {
            return LayoutElement.super.getRectangle();
        }
    }

    public static class Holder<T extends LegacySelectionList<?>> extends AbstractWidget implements ContainerEventHandler, WidgetAndType<T> {
        private final T list;

        public Holder(T list) {
            super(0, 0, 100, 0, CommonComponents.EMPTY);
            this.list = list;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float deltaTick) {
            this.list.render(guiGraphics, mouseX, mouseY, deltaTick);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            this.list.updateNarration(output);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.list);
        }

        public T getList() {
            return list;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button /^? if >=1.21.9 {^/ ,boolean doubleClick /^?}^/) {
            return this.list.mouseClicked(mouseX, mouseY, button /^? if >=1.21.9 {^/ ,doubleClick /^?}^/);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return this.list.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return this.list.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
            System.out.println("Legacy list scrolled: " + horizontal + ", " + vertical);
            return this.list.mouseScrolled(mouseX, mouseY, horizontal, vertical);
        }

        @Override
        public boolean keyPressed(int i, int j, int k) {
            return this.list.keyPressed(i, j, k);
        }

        @Override
        public boolean charTyped(char c, int i) {
            return this.list.charTyped(c, i);
        }

        @Override
        public boolean isDragging() {
            return this.list.isDragging();
        }

        @Override
        public void setDragging(boolean dragging) {
            this.list.setDragging(dragging);
        }

        @Nullable
        @Override
        public GuiEventListener getFocused() {
            return this.list.getFocused();
        }

        @Override
        public void setFocused(@Nullable GuiEventListener listener) {
            this.list.setFocused(listener);
        }

        @Nullable
        @Override
        public ComponentPath nextFocusPath(FocusNavigationEvent event) {
            return this.list.nextFocusPath(event);
        }

        @Nullable
        @Override
        public ComponentPath getCurrentFocusPath() {
            return this.list.getCurrentFocusPath();
        }

        @Override
        public void setX(int x) {
            this.list.setX(x);
        }

        @Override
        public void setY(int y) {
            this.list.setY(y);
        }

        @Override
        public int getX() {
            return this.list.getX();
        }

        @Override
        public int getY() {
            return this.list.getY();
        }

        @Override
        public void setWidth(int width) {
            this.list.setWidth(width);
        }

        @Override
        public void setHeight(int height) {
            this.list.setHeight(height);
        }

        @Override
        public int getWidth() {
            return this.list.getWidth();
        }

        @Override
        public int getHeight() {
            return this.list.getHeight();
        }

        @Override
        public void visitWidgets(Consumer<AbstractWidget> consumer) {
            this.list.visitWidgets(consumer);
        }

        @Override
        public T getType() {
            return this.list;
        }

        @Override
        public AbstractWidget getWidget() {
            return this;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            // AbstractWidget impl uses the fields width and height, we override getWidth and getHeight
            // this allows Screen#getChildAt to work, which means scrolling will work.
            return this.isActive()
                   && this.visible
                   && mouseX >= this.getX()
                   && mouseY >= this.getY()
                   && mouseX < this.getX() + this.getWidth()
                   && mouseY < this.getY() + this.getHeight()   ;
        }
    }
}
*///?}
