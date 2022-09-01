package dev.isxander.yacl.gui.controllers;

import dev.isxander.yacl.api.ButtonOption;
import dev.isxander.yacl.api.Control;
import dev.isxander.yacl.api.utils.Dimension;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ActionControl implements Control<Runnable> {
    private final ButtonOption option;
    private final Text executeText;

    public ActionControl(ButtonOption option) {
        this(option, Text.translatable("yacl.control.action.execute"));
    }

    public ActionControl(ButtonOption option, Text executeText) {
        this.option = option;
        this.executeText = executeText;

    }

    @Override
    public ButtonOption option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return executeText;
    }

    @Override
    public ControlWidget<ActionControl> provideWidget(Screen screen, Dimension<Integer> widgetDimension) {
        return new ActionControlElement(this, screen, widgetDimension);
    }

    public static class ActionControlElement extends ControlWidget<ActionControl> {
        public ActionControlElement(ActionControl control, Screen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                playDownSound();
                control.option().action().run();
                return true;
            }
            return false;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }
    }
}
