package dev.isxander.yacl3.gui.controllers;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

/**
 * Simple controller that simply runs the button action on press
 * and renders a {@link} Text on the right.
 */
public class ActionController implements Controller<BiConsumer<YACLScreen, ButtonOption>> {
    public static final Component DEFAULT_TEXT = Component.translatable("yacl.control.action.execute");

    private final ButtonOption option;
    private final Component text;

    /**
     * Constructs an action controller
     * with the default formatter of {@link ActionController#DEFAULT_TEXT}
     *
     * @param option bound option
     */
    public ActionController(ButtonOption option) {
        this(option, DEFAULT_TEXT);
    }

    /**
     * Constructs an action controller
     *
     * @param option bound option
     * @param text text to display
     */
    public ActionController(ButtonOption option, Component text) {
        this.option = option;
        this.text = text;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ButtonOption option() {
        return option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component formatValue() {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new ActionControllerElement(this, screen, widgetDimension);
    }

    public static class ActionControllerElement extends ControllerWidget<ActionController> {
        private final String buttonString;

        public ActionControllerElement(ActionController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
            buttonString = control.formatValue().getString().toLowerCase();
        }

        public void executeAction() {
            playDownSound();
            control.option().action().accept(screen, control.option());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY) && isAvailable()) {
                executeAction();
                return true;
            }
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!focused) {
                return false;
            }

            if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_SPACE || keyCode == InputConstants.KEY_NUMPADENTER) {
                executeAction();
                return true;
            }

            return false;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public boolean canReset() {
            return false;
        }

        @Override
        public boolean matchesSearch(String query) {
            return super.matchesSearch(query) || buttonString.contains(query);
        }
    }
}
