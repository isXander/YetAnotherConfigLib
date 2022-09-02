package dev.isxander.yacl.gui;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
import dev.isxander.yacl.impl.YACLConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

import java.util.Collections;
import java.util.List;

public class OptionListWidget extends ElementListWidget<OptionListWidget.Entry> {

    public OptionListWidget(ConfigCategory category, YACLScreen screen, MinecraftClient client, int width, int height) {
        super(client, width / 3 * 2, height, 0, height, 22);
        left = width - this.width;
        right = width;

        for (OptionGroup group : category.groups()) {
            if (!group.isRoot())
                addEntry(new GroupSeparatorEntry(group, screen));
            for (Option<?> option : group.options()) {
                addEntry(new OptionEntry(option.controller().provideWidget(screen, null)));
            }
        }
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
    protected int getScrollbarPositionX() {
        return left + super.getScrollbarPositionX();
    }

    @Override
    protected void renderBackground(MatrixStack matrices) {
        setRenderBackground(client.world == null);
        if (client.world != null)
            fill(matrices, left, top, right, bottom, 0x6B000000);
    }

    public static abstract class Entry extends ElementListWidget.Entry<Entry> {

    }

    private static class OptionEntry extends Entry {
        private final ControllerWidget<?> widget;

        public OptionEntry(ControllerWidget<?> widget) {
            this.widget = widget;
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

        private float hoveredTicks = 0;
        private int prevMouseX, prevMouseY;

        private final Screen screen;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        public GroupSeparatorEntry(OptionGroup group, Screen screen) {
            this.group = group;
            this.screen = screen;
            this.wrappedTooltip = textRenderer.wrapLines(group.tooltip(), screen.width / 2);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
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
            return Collections.emptyList();
        }


    }
}
