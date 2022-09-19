package dev.isxander.yacl.gui.controllers;

import dev.isxander.yacl.api.ButtonOption;
import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;

/**
 * Simple controller that simply runs the button action on press
 * and renders a {@link} Text on the right.
 */
public class ActionController implements Controller<BiConsumer<YACLScreen, ButtonOption>> {
    public static final Text DEFAULT_TEXT = Text.translatable("yacl.control.action.execute");

    private final ButtonOption option;
    private final Text text;

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
    public ActionController(ButtonOption option, Text text) {
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
    public Text formatValue() {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new ActionControllerElement(this, screen, widgetDimension);
    }

    @ApiStatus.Internal
    public static class ActionControllerElement extends ControllerWidget<ActionController> {
        public ActionControllerElement(ActionController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
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

            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                executeAction();
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
