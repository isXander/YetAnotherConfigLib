package dev.isxander.yacl3.gui.controllers;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

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
        protected void extractValueText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
            super.extractValueText(graphics, mouseX, mouseY, a);

            if (hovered) {
                graphics.requestCursor(isAvailable() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
            }
        }

        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
            if (isMouseOver(event.x(), event.y()) && isAvailable()) {
                executeAction();
                return true;
            }
            return false;
        }

        @Override
        public boolean keyPressed(@NonNull KeyEvent event) {
            if (!focused) {
                return false;
            }

            if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_SPACE || event.key() == InputConstants.KEY_NUMPADENTER) {
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
