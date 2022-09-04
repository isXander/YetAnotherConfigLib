package dev.isxander.yacl.gui;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.impl.YACLConstants;
import net.minecraft.client.MinecraftClient;
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
                addEntry(new OptionEntry(option.controller().provideWidget(screen, null), viewableSupplier));
            }
        }
    }

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

        return super.mouseScrolled(mouseX, mouseY, amount);
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

    public static abstract class Entry extends ElementListWidget.Entry<Entry> {
        public boolean isViewable() {
            return true;
        }
    }

    private static class OptionEntry extends Entry {
        public final AbstractWidget widget;
        private final Supplier<Boolean> viewableSupplier;

        public OptionEntry(AbstractWidget widget, Supplier<Boolean> viewableSupplier) {
            this.widget = widget;
            this.viewableSupplier = viewableSupplier;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            widget.setDimension(Dimension.ofInt(x, y, entryWidth, 20));

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
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(widget);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(widget);
        }
    }

    private static class GroupSeparatorEntry extends Entry {
        private final OptionGroup group;
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

            drawCenteredText(matrices, textRenderer, group.name(), x + entryWidth / 2, y + entryHeight / 2 - textRenderer.fontHeight / 2, -1);

            if (hoveredTicks >= YACLConstants.HOVER_TICKS) {
                screen.renderOrderedTooltip(matrices, wrappedTooltip, x - 6, y + entryHeight);
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
