package dev.isxander.yacl.gui.controllers;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * On hover, this controller renders a tickbox, otherwise, formatted {@link Text}
 */
public class TickBoxController implements Controller<Boolean> {

    public static final Function<Boolean, Text> ON_OFF_FORMATTER = (state) ->
            state
                    ? Text.translatable("yacl.control.tickbox.on").formatted(Formatting.GREEN)
                    : Text.translatable("yacl.control.tickbox.off").formatted(Formatting.RED);

    public static final Function<Boolean, Text> TRUE_FALSE_FORMATTER = (state) ->
            state
                    ? Text.translatable("yacl.control.tickbox.true").formatted(Formatting.GREEN)
                    : Text.translatable("yacl.control.tickbox.false").formatted(Formatting.RED);

    public static final Function<Boolean, Text> YES_NO_FORMATTER = (state) ->
            state
                    ? Text.translatable("yacl.control.tickbox.yes").formatted(Formatting.GREEN)
                    : Text.translatable("yacl.control.tickbox.no").formatted(Formatting.RED);

    private final Option<Boolean> option;
    private final Function<Boolean, Text> valueFormatter;

    /**
     * Constructs a tickbox controller
     * with the default value formatter of {@link TickBoxController#ON_OFF_FORMATTER}
     *
     * @param option bound option
     */
    public TickBoxController(Option<Boolean> option) {
        this(option, ON_OFF_FORMATTER);
    }

    /**
     * Constructs a tickbox controller
     *
     * @param option bound option
     * @param valueFormatter format value into any {@link Text}
     */
    public TickBoxController(Option<Boolean> option, Function<Boolean, Text> valueFormatter) {
        this.option = option;
        this.valueFormatter = valueFormatter;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<Boolean> option() {
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
     * {@inheritDoc}
     */
    @Override
    public ControllerWidget<TickBoxController> provideWidget(Screen screen, Dimension<Integer> widgetDimension) {
        return new TickBoxControllerElement(this, screen, widgetDimension);
    }

    @ApiStatus.Internal
    public static class TickBoxControllerElement extends ControllerWidget<TickBoxController> {
        private TickBoxControllerElement(TickBoxController control, Screen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        protected void drawHoveredControl(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            int outlineSize = 10;
            int outlineX1 = dim.xLimit() - getXPadding() - outlineSize;
            int outlineY1 = dim.centerY() - outlineSize / 2;
            int outlineX2 = dim.xLimit() - getXPadding();
            int outlineY2 = dim.centerY() + outlineSize / 2;
            drawOutline(matrices, outlineX1 + 1, outlineY1 + 1, outlineX2 + 1, outlineY2 + 1, 1, 0xFF404040);
            drawOutline(matrices, outlineX1, outlineY1, outlineX2, outlineY2, 1, -1);
            if (control.option().pendingValue()) {
                DrawableHelper.fill(matrices, outlineX1 + 3, outlineY1 + 3, outlineX2 - 1, outlineY2 - 1, 0xFF404040);
                DrawableHelper.fill(matrices, outlineX1 + 2, outlineY1 + 2, outlineX2 - 2, outlineY2 - 2, -1);
            }
        }

        @Override
        protected void drawValueText(MatrixStack matrices) {
            if (!hovered)
                super.drawValueText(matrices);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY))
                return false;

            control.option().requestSet(!control.option().pendingValue());
            playDownSound();
            return true;
        }

        @Override
        protected int getHoveredControlWidth() {
            return 10;
        }
    }
}
