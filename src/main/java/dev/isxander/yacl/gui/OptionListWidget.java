package dev.isxander.yacl.gui;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.controllers.ControlWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class OptionListWidget extends ElementListWidget<OptionListWidget.Entry> {

    public OptionListWidget(ConfigCategory category, YACLScreen screen, MinecraftClient client, int width, int height) {
        super(client, width / 3 * 2, height, 0, height, 22);
        left = width - this.width;
        right = width;

        for (OptionGroup group : category.groups()) {
            if (!group.isRoot())
                addEntry(new GroupSeparatorEntry(group));
            for (Option<?> option : group.options()) {
                addEntry(new OptionEntry(option.control().provideWidget(screen, null)));
            }
        }
    }

    @Override
    protected int getScrollbarPositionX() {
        return left + super.getScrollbarPositionX();
    }

    public static abstract class Entry extends ElementListWidget.Entry<Entry> {

    }

    private static class OptionEntry extends Entry {
        private final ControlWidget<?> widget;

        public OptionEntry(ControlWidget<?> widget) {
            this.widget = widget;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            widget.dim = Dimension.ofInt(x, y, entryWidth, 20);

            widget.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(widget);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(widget);
        }
    }

    private static class GroupSeparatorEntry extends Entry {
        private final OptionGroup group;

        public GroupSeparatorEntry(OptionGroup group) {
            this.group = group;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            drawCenteredText(matrices, textRenderer, group.name(), x + entryWidth / 2, y + entryHeight / 2 - textRenderer.fontHeight / 2, -1);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }
    }
}
