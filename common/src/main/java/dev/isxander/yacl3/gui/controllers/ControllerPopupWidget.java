package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public abstract class ControllerPopupWidget extends ControllerWidget<Controller<?>> implements GuiEventListener {
    public final ControllerWidget<?> entryWidget;
    public ControllerPopupWidget(Controller<?> control, YACLScreen screen, Dimension<Integer> dim, ControllerWidget<?> entryWidget) {
        super(control, screen, dim);
        this.entryWidget = entryWidget;
    }

    public ControllerWidget<?> entryWidget() {
        return entryWidget;
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return entryWidget.keyPressed(keyCode, scanCode, modifiers);
    }

    public void close() {
        screen.clearPopupControllerWidget();
    }

    public Component popupTitle() {
        return Component.translatable("yacl.control.text.blank");
    }

    @Override
    protected int getHoveredControlWidth() {
        return 0;
    }

}
