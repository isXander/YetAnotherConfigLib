package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public abstract class ControllerPopupWidget<T extends Controller<?>> extends ControllerWidget<Controller<?>> implements GuiEventListener {
    public final ControllerWidget<?> entryWidget;
    public ControllerPopupWidget(T control, YACLScreen screen, Dimension<Integer> dim, ControllerWidget<?> entryWidget) {
        super(control, screen, dim);
        this.entryWidget = entryWidget;
    }

    public ControllerWidget<?> entryWidget() {
        return entryWidget;
    }

    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        return entryWidget.keyPressed(event);
    }

    public void close() {}

    public Component popupTitle() {
        return Component.translatable("yacl.control.text.blank");
    }

    @Override
    protected int getHoveredControlWidth() {
        return 0;
    }

}
