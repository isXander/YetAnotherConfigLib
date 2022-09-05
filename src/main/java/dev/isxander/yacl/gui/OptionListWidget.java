package dev.isxander.yacl.gui;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.impl.YACLConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class OptionListWidget extends ElementListWidget<OptionListWidget.Entry> {

    public OptionListWidget(ConfigCategory category, YACLScreen screen, MinecraftClient client, int width, int height) {
        super(client, width / 3 * 2, height, 0, height, 22);
        left = width - this.width;
        right = width;

        for (OptionGroup group : category.groups()) {
            Supplier<Boolean> viewableSupplier;
            if (!group.isRoot()) {
                GroupSeparatorEntry groupSeparatorEntry = new GroupSeparatorEntry(group, screen);
                viewableSupplier = groupSeparatorEntry::isExpanded;
                addEntry(groupSeparatorEntry);
            } else {
                viewableSupplier = () -> true;
            }

            for (Option<?> option : group.options()) {
                addEntry(new OptionEntry(option.controller().provideWidget(screen, Dimension.ofInt(getRowLeft(), 0, getRowWidth(), 20)), viewableSupplier));
            }
        }
    }

    /*
      below code is licensed from cloth-config under LGPL3
      modified to inherit vanilla's EntryListWidget and use yarn mappings
    */

    @Nullable
    @Override
    protected Entry getEntryAtPosition(double x, double y) {
        int listMiddleX = this.left + this.width / 2;
        int minX = listMiddleX - this.getRowWidth() / 2;
        int maxX = listMiddleX + this.getRowWidth() / 2;
        int currentY = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int itemY = 0;
        int itemIndex = -1;
        for (int i = 0; i < children().size(); i++) {
            Entry item = children().get(i);
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
        return children().stream().map(Entry::getItemHeight).reduce(0, Integer::sum) + headerHeight;
    }

    @Override
    protected void centerScrollOn(Entry entry) {
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
            Entry entry = children().get(i);
            int top = this.getRowTop(i);
            int bottom = top + entry.getItemHeight();
            int entryHeight = entry.getItemHeight() - 4;
            if (bottom >= this.top && top <= this.bottom) {
                this.renderEntry(matrices, mouseX, mouseY, delta, i, left, top, right, entryHeight);
            }
        }
    }

    /* END cloth config code */

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Entry child : children()) {
            if (child != getEntryAtPosition(mouseX, mouseY) && child instanceof OptionEntry optionEntry)
                optionEntry.widget.unfocus();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (Entry child : children()) {
            if (child.mouseScrolled(mouseX, mouseY, amount))
                return true;
        }

        this.setScrollAmount(this.getScrollAmount() - amount * (double) (getMaxScroll() / getEntryCount()) / 2.0D);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Entry child : children()) {
            if (child.keyPressed(keyCode, scanCode, modifiers))
                return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (Entry child : children()) {
            if (child.charTyped(chr, modifiers))
                return true;
        }

        return super.charTyped(chr, modifiers);
    }

    @Override
    protected int getScrollbarPositionX() {
        return left + super.getScrollbarPositionX();
    }

    @Override
    protected void renderBackground(MatrixStack matrices) {
        setRenderBackground(client.world == null);
        if (client.world != null)
            fill(matrices, left, top, right, bottom, 0x6B000000);
    }

    @Override
    public List<Entry> children() {
        return super.children().stream().filter(Entry::isViewable).toList();
    }

    public abstract class Entry extends ElementListWidget.Entry<Entry> {
        public boolean isViewable() {
            return true;
        }

        public int getItemHeight() {
            return 22;
        }
    }

    private class OptionEntry extends Entry {
        public final AbstractWidget widget;
        private final Supplier<Boolean> viewableSupplier;

        public OptionEntry(AbstractWidget widget, Supplier<Boolean> viewableSupplier) {
            this.widget = widget;
            this.viewableSupplier = viewableSupplier;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            widget.setDimension(widget.getDimension().setY(y));

            widget.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            return widget.mouseScrolled(mouseX, mouseY, amount);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return widget.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return widget.charTyped(chr, modifiers);
        }

        @Override
        public boolean isViewable() {
            return viewableSupplier.get();
        }

        @Override
        public int getItemHeight() {
            return widget.getDimension().height() + 2;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(widget);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(widget);
        }
    }

    private class GroupSeparatorEntry extends Entry {
        private final OptionGroup group;
        private final MultilineText wrappedName;
        private final List<OrderedText> wrappedTooltip;

        private final ButtonWidget expandMinimizeButton;

        private float hoveredTicks = 0;
        private int prevMouseX, prevMouseY;

        private final Screen screen;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        private boolean groupExpanded;

        public GroupSeparatorEntry(OptionGroup group, Screen screen) {
            this.group = group;
            this.screen = screen;
            this.wrappedName = MultilineText.create(textRenderer, group.name(), getRowWidth() - 45);
            //this.wrappedName = textRenderer.wrapLines(group.name(), getRowWidth() - 45);
            this.wrappedTooltip = textRenderer.wrapLines(group.tooltip(), screen.width / 2);
            this.groupExpanded = !group.collapsed();
            this.expandMinimizeButton = new ButtonWidget(0, 0, 20, 20, Text.empty(), btn -> {
                groupExpanded = !groupExpanded;
                updateExpandMinimizeText();
            });
            updateExpandMinimizeText();
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            expandMinimizeButton.x = x + entryWidth - expandMinimizeButton.getWidth();
            expandMinimizeButton.y = y + entryHeight / 2 - expandMinimizeButton.getHeight() / 2;
            if (hovered)
                expandMinimizeButton.render(matrices, mouseX, mouseY, tickDelta);

            hovered &= !expandMinimizeButton.isMouseOver(mouseX, mouseY);
            if (hovered && (!YACLConstants.HOVER_MOUSE_RESET || (mouseX == prevMouseX && mouseY == prevMouseY)))
                hoveredTicks += tickDelta;
            else
                hoveredTicks = 0;

            wrappedName.drawCenterWithShadow(matrices, x + entryWidth / 2, y + getYPadding());

            if (hoveredTicks >= YACLConstants.HOVER_TICKS) {
                screen.renderOrderedTooltip(matrices, wrappedTooltip, x - 6, y + entryHeight / 2 + 6 + (wrappedTooltip.size() * textRenderer.fontHeight) / 2);
            }

            prevMouseX = mouseX;
            prevMouseY = mouseY;
        }

        public boolean isExpanded() {
            return groupExpanded;
        }

        private void updateExpandMinimizeText() {
            expandMinimizeButton.setMessage(Text.of(isExpanded() ? "\u25BC" : "\u25C0"));
        }

        @Override
        public int getItemHeight() {
            return wrappedName.count() * textRenderer.fontHeight + getYPadding() * 2;
        }

        private int getYPadding() {
            return 6;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(new Selectable() {
                @Override
                public Selectable.SelectionType getType() {
                    return Selectable.SelectionType.HOVERED;
                }

                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                    builder.put(NarrationPart.TITLE, group.name());
                }
            });
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(expandMinimizeButton);
        }
    }
}
