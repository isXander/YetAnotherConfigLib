package dev.isxander.yacl.gui.controllers;

import dev.isxander.yacl.api.ListOption;
import dev.isxander.yacl.api.ListOptionEntry;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.*;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListEntryWidget extends AbstractWidget implements ParentElement {
    private final TooltipButtonWidget removeButton, moveUpButton, moveDownButton;
    private final AbstractWidget entryWidget;

    private final ListOption<?> listOption;

    private final String optionNameString;

    private Element focused;
    private boolean dragging;

    public ListEntryWidget(YACLScreen screen, ListOptionEntry<?> listOptionEntry, AbstractWidget entryWidget) {
        super(entryWidget.getDimension());
        this.listOption = listOptionEntry.parentGroup();
        this.optionNameString = listOptionEntry.name().getString().toLowerCase();
        this.entryWidget = entryWidget;

        Dimension<Integer> dim = entryWidget.getDimension();
        entryWidget.setDimension(dim.clone().move(20 * 2, 0).expand(-20 * 3, 0));

        removeButton = new TooltipButtonWidget(screen, dim.xLimit() - 20, dim.y(), dim.height(), dim.height(), Text.of("\u274c"), Text.translatable("yacl.list.remove"), btn -> {
            listOption.removeEntry(listOptionEntry);
        });

        moveUpButton = new TooltipButtonWidget(screen, dim.x(), dim.y(), dim.height(), dim.height(), Text.of("\u2191"), Text.translatable("yacl.list.move_up"), btn -> {
            int index = listOption.indexOf(listOptionEntry) - 1;
            if (index >= 0) {
                listOption.removeEntry(listOptionEntry);
                listOption.insertEntry(index, listOptionEntry);
                btn.active = index > 0;
            }
        });
        moveUpButton.active = listOption.indexOf(listOptionEntry) > 0;

        moveDownButton = new TooltipButtonWidget(screen, dim.x() + 20, dim.y(), dim.height(), dim.height(), Text.of("\u2193"), Text.translatable("yacl.list.move_down"), btn -> {
            int index = listOption.indexOf(listOptionEntry) + 1;
            if (index < listOption.options().size()) {
                listOption.removeEntry(listOptionEntry);
                listOption.insertEntry(index, listOptionEntry);
                btn.active = index < listOption.options().size() - 1;
            }
        });
        moveDownButton.active = listOption.indexOf(listOptionEntry) < listOptionEntry.parentGroup().options().size() - 1;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        removeButton.setY(getDimension().y());
        moveUpButton.setY(getDimension().y());
        moveDownButton.setY(getDimension().y());
        entryWidget.setDimension(entryWidget.getDimension().withY(getDimension().y()));

        removeButton.render(matrices, mouseX, mouseY, delta);
        moveUpButton.render(matrices, mouseX, mouseY, delta);
        moveDownButton.render(matrices, mouseX, mouseY, delta);
        entryWidget.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        removeButton.renderHoveredTooltip(matrices);
        moveUpButton.renderHoveredTooltip(matrices);
        moveDownButton.renderHoveredTooltip(matrices);
    }

    @Override
    public void unfocus() {
        entryWidget.unfocus();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        entryWidget.appendNarrations(builder);
    }

    @Override
    public boolean matchesSearch(String query) {
        return optionNameString.contains(query.toLowerCase());
    }

    @Override
    public List<? extends Element> children() {
        return List.of(moveUpButton, moveDownButton, entryWidget, removeButton);
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Nullable
    @Override
    public Element getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        this.focused = focused;
    }
}
