package dev.isxander.yacl.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class ElementListWidgetExt<E extends ElementListWidgetExt.Entry<E>> extends ElementListWidget<E> {
    protected final int x, y;

    private double smoothScrollAmount = getScrollAmount();
    private boolean returnSmoothAmount = false;
    private final boolean doSmoothScrolling;

    public ElementListWidgetExt(MinecraftClient client, int x, int y, int width, int height, boolean smoothScrolling) {
        super(client, width, height, y, y + height, 22);
        this.x = x;
        this.y = y;
        this.left = x;
        this.right = this.left + width;
        this.doSmoothScrolling = smoothScrolling;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        // default implementation bases scroll step from total height of entries, this is constant
        this.setScrollAmount(this.getScrollAmount() - amount * 20);
        return true;
    }

    @Override
    protected void renderBackground(MatrixStack matrices) {
        // render transparent background if in-game.
        setRenderBackground(client.world == null);
        if (client.world != null)
            fill(matrices, left, top, right, bottom, 0x6B000000);
    }

    @Override
    protected int getScrollbarPositionX() {
        // default implementation does not respect left/right
        return this.right - 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        smoothScrollAmount = MathHelper.lerp(MinecraftClient.getInstance().getLastFrameDuration() * 0.5, smoothScrollAmount, getScrollAmount());
        returnSmoothAmount = true;
        super.render(matrices, mouseX, mouseY, delta);
        returnSmoothAmount = false;
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

    public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (E entry : children()) {
            entry.postRender(matrices, mouseX, mouseY, delta);
        }
    }

    /*
      below code is licensed from cloth-config under LGPL3
      modified to inherit vanilla's EntryListWidget and use yarn mappings

      code is responsible for having dynamic item heights
    */

    @Nullable
    @Override
    protected E getEntryAtPosition(double x, double y) {
        int listMiddleX = this.left + this.width / 2;
        int minX = listMiddleX - this.getRowWidth() / 2;
        int maxX = listMiddleX + this.getRowWidth() / 2;
        int currentY = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int itemY = 0;
        int itemIndex = -1;
        for (int i = 0; i < children().size(); i++) {
            E item = children().get(i);
            itemY += item.getItemHeight();
            if (itemY > currentY) {
                itemIndex = i;
                break;
            }
        }
        return x < (double) this.getScrollbarPositionX() && x >= minX && y <= maxX && itemIndex >= 0 && currentY >= 0 && itemIndex < this.getEntryCount() ? this.children().get(itemIndex) : null;
    }

    @Override
    protected int getMaxPosition() {
        return children().stream().map(E::getItemHeight).reduce(0, Integer::sum) + headerHeight;
    }

    @Override
    protected void centerScrollOn(E entry) {
        double d = (this.bottom - this.top) / -2d;
        for (int i = 0; i < this.children().indexOf(entry) && i < this.getEntryCount(); i++)
            d += children().get(i).getItemHeight();
        this.setScrollAmount(d);
    }

    @Override
    protected int getRowTop(int index) {
        int integer = top + 4 - (int) this.getScrollAmount() + headerHeight;
        for (int i = 0; i < children().size() && i < index; i++)
            integer += children().get(i).getItemHeight();
        return integer;
    }

    @Override
    protected void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int left = this.getRowLeft();
        int right = this.getRowWidth();
        int count = this.getEntryCount();

        for(int i = 0; i < count; ++i) {
            E entry = children().get(i);
            int top = this.getRowTop(i);
            int bottom = top + entry.getItemHeight();
            int entryHeight = entry.getItemHeight() - 4;
            if (bottom >= this.top && top <= this.bottom) {
                this.renderEntry(matrices, mouseX, mouseY, delta, i, left, top, right, entryHeight);
            }
        }
    }

    /* END cloth config code */

    public abstract static class Entry<E extends ElementListWidgetExt.Entry<E>> extends ElementListWidget.Entry<E> {
        public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        }

        public int getItemHeight() {
            return 22;
        }
    }
}
