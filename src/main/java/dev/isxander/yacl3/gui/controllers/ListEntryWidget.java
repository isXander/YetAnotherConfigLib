package dev.isxander.yacl3.gui.controllers;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.ListOptionEntry;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TooltipButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListEntryWidget extends AbstractWidget implements ContainerEventHandler {
    private final TooltipButtonWidget removeButton, moveUpButton, moveDownButton;
    private final AbstractWidget entryWidget;

    private final ListOption<?> listOption;
    private final ListOptionEntry<?> listOptionEntry;

    private final String optionNameString;

    private GuiEventListener focused;
    private boolean dragging;

    public ListEntryWidget(YACLScreen screen, ListOptionEntry<?> listOptionEntry, AbstractWidget entryWidget) {
        super(entryWidget.getDimension().withHeight(Math.min(entryWidget.getDimension().height(), 20) - ((listOptionEntry.parentGroup().indexOf(listOptionEntry) == listOptionEntry.parentGroup().options().size() - 1) ? 0 : 2))); // -2 to remove the padding
        this.listOptionEntry = listOptionEntry;
        this.listOption = listOptionEntry.parentGroup();
        this.optionNameString = listOptionEntry.name().getString().toLowerCase();
        this.entryWidget = entryWidget;

        Dimension<Integer> dim = entryWidget.getDimension();
        entryWidget.setDimension(dim.clone().move(20 * 2, 0).expand(-20 * 3, 0));

        removeButton = new TooltipButtonWidget(screen, dim.xLimit() - 20, dim.y(), 20, 20, Component.literal("\u274c"), Component.translatable("yacl.list.remove"), btn -> {
            listOption.removeEntry(listOptionEntry);
            updateButtonStates();
        });

        moveUpButton = new TooltipButtonWidget(screen, dim.x(), dim.y(), 20, 20, Component.literal("\u2191"), Component.translatable("yacl.list.move_up"), btn -> {
            int index = listOption.indexOf(listOptionEntry) - 1;
            if (index >= 0) {
                listOption.removeEntry(listOptionEntry);
                listOption.insertEntry(index, listOptionEntry);
                updateButtonStates();
            }
        });

        moveDownButton = new TooltipButtonWidget(screen, dim.x() + 20, dim.y(), 20, 20, Component.literal("\u2193"), Component.translatable("yacl.list.move_down"), btn -> {
            int index = listOption.indexOf(listOptionEntry) + 1;
            if (index < listOption.options().size()) {
                listOption.removeEntry(listOptionEntry);
                listOption.insertEntry(index, listOptionEntry);
                updateButtonStates();
            }
        });

        updateButtonStates();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        updateButtonStates(); // update every render in case option becomes available/unavailable

        removeButton.setY(getDimension().y());
        moveUpButton.setY(getDimension().y());
        moveDownButton.setY(getDimension().y());
        entryWidget.setDimension(entryWidget.getDimension().withY(getDimension().y()));

        removeButton.render(graphics, mouseX, mouseY, delta);
        moveUpButton.render(graphics, mouseX, mouseY, delta);
        moveDownButton.render(graphics, mouseX, mouseY, delta);
        entryWidget.render(graphics, mouseX, mouseY, delta);
    }

    protected void updateButtonStates() {
        removeButton.active = listOption.available() && listOption.numberOfEntries() > listOption.minimumNumberOfEntries();
        moveUpButton.active = listOption.indexOf(listOptionEntry) > 0 && listOption.available();
        moveDownButton.active = listOption.indexOf(listOptionEntry) < listOption.options().size() - 1 && listOption.available();
    }

    @Override
    public void unfocus() {
        entryWidget.unfocus();
    }

    @Override
    public void updateNarration(NarrationElementOutput builder) {
        entryWidget.updateNarration(builder);
    }

    @Override
    public boolean matchesSearch(String query) {
        return optionNameString.contains(query.toLowerCase());
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return ImmutableList.of(moveUpButton, moveDownButton, entryWidget, removeButton);
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
    public GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.focused = focused;
    }

    //? if >=1.21.9 {
    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
        return ContainerEventHandler.super.mouseClicked(mouseButtonEvent, doubleClick);
    }
    //?} else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button);
    }
    *///?}

    //? if >=1.21.9 {
    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent mouseButtonEvent) {
        return ContainerEventHandler.super.mouseReleased(mouseButtonEvent);
    }
    //?} else {
    /*@Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return ContainerEventHandler.super.mouseReleased(mouseX, mouseY, button);
    }
    *///?}

    //? if >=1.21.9 {
    @Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent mouseButtonEvent, double dx, double dy) {
        return ContainerEventHandler.super.mouseDragged(mouseButtonEvent, dx, dy);
    }
    //?} else {
    /*@Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        return ContainerEventHandler.super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }
    *///?}

    //? if >=1.21.9 {
    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyEvent keyEvent) {
        return ContainerEventHandler.super.keyPressed(keyEvent);
    }
    //?} else {
    /*@Override
    public boolean keyPressed(int keycode, int scancode, int modifiers) {
        return ContainerEventHandler.super.keyPressed(keycode, scancode, modifiers);
    }
    *///?}

    //? if >=1.21.9 {
    @Override
    public boolean keyReleased(net.minecraft.client.input.KeyEvent keyEvent) {
        return ContainerEventHandler.super.keyReleased(keyEvent);
    }
    //?} else {
    /*@Override
    public boolean keyReleased(int keycode, int scancode, int modifiers) {
        return ContainerEventHandler.super.keyReleased(keycode, scancode, modifiers);
    }
    *///?}

    //? if >=1.21.9 {
    @Override
    public boolean charTyped(net.minecraft.client.input.CharacterEvent characterEvent) {
        return ContainerEventHandler.super.charTyped(characterEvent);
    }
    //?} else {
    /*@Override
    public boolean charTyped(char codePoint, int modifiers) {
        return ContainerEventHandler.super.charTyped(codePoint, modifiers);
    }
    *///?}
}
