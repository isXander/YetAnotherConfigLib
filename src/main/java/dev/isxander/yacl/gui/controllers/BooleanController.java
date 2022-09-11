package dev.isxander.yacl.gui.controllers;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.function.Function;

/**
 * This controller renders a simple formatted {@link Text}
 */
public class BooleanController implements Controller<Boolean> {

    public static final Function<Boolean, Text> ON_OFF_FORMATTER = (state) ->
            state
                    ? Text.translatable("options.on")
                    : Text.translatable("options.off");

    public static final Function<Boolean, Text> TRUE_FALSE_FORMATTER = (state) ->
            state
                    ? Text.translatable("yacl.control.boolean.true")
                    : Text.translatable("yacl.control.boolean.false");

    public static final Function<Boolean, Text> YES_NO_FORMATTER = (state) ->
            state
                    ? Text.translatable("gui.yes")
                    : Text.translatable("gui.no");

    private final Option<Boolean, ?> option;
    private final Function<Boolean, Text> valueFormatter;
    private final boolean coloured;

    /**
     * Constructs a tickbox controller
     * with the default value formatter of {@link BooleanController#ON_OFF_FORMATTER}
     *
     * @param option bound option
     */
    public BooleanController(Option<Boolean, ?> option) {
        this(option, ON_OFF_FORMATTER, false);
    }

    /**
     * Constructs a tickbox controller
     * with the default value formatter of {@link BooleanController#ON_OFF_FORMATTER}
     *
     * @param option bound option
     * @param coloured value format is green or red depending on the state
     */
    public BooleanController(Option<Boolean, ?> option, boolean coloured) {
        this(option, ON_OFF_FORMATTER, coloured);
    }

    /**
     * Constructs a tickbox controller
     *
     * @param option bound option
     * @param valueFormatter format value into any {@link Text}
     * @param coloured value format is green or red depending on the state
     */
    public BooleanController(Option<Boolean, ?> option, Function<Boolean, Text> valueFormatter, boolean coloured) {
        this.option = option;
        this.valueFormatter = valueFormatter;
        this.coloured = coloured;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<Boolean, ?> option() {
        return option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Text formatValue() {
        return valueFormatter.apply(option().pendingValue());
    }

    /**
     * Value format is green or red depending on the state
     */
    public boolean coloured() {
        return coloured;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new BooleanControllerElement(this, screen, widgetDimension);
    }

    @ApiStatus.Internal
    public static class BooleanControllerElement extends ControllerWidget<BooleanController> {
        private BooleanControllerElement(BooleanController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        protected void drawHoveredControl(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY))
                return false;

            toggleSetting();
            return true;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        public void toggleSetting() {
            control.option().requestSet(!control.option().pendingValue());
            playDownSound();
        }

        @Override
        protected Text getValueText() {
            if (control.coloured()) {
                return super.getValueText().copy().formatted(control.option().pendingValue() ? Formatting.GREEN : Formatting.RED);
            }

            return super.getValueText();
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!focused) {
                return false;
            }

            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                toggleSetting();
                return true;
            }

            return false;
        }
    }
}
