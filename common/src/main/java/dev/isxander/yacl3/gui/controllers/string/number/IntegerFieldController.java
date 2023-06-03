package dev.isxander.yacl3.gui.controllers.string.number;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

/**
 * {@inheritDoc}
 */
public class IntegerFieldController extends NumberFieldController<Integer> {
    private final int min, max;

    /**
     * Constructs a integer field controller
     *
     * @param option option to bind controller to
     * @param min minimum allowed value (clamped on apply)
     * @param max maximum allowed value (clamped on apply)
     * @param formatter display text, not used whilst editing
     */
    public IntegerFieldController(Option<Integer> option, int min, int max, Function<Integer, Component> formatter) {
        super(option, formatter);
        this.min = min;
        this.max = max;
    }

    /**
     * Constructs a integer field controller.
     * Uses {@link IntegerSliderController#DEFAULT_FORMATTER} as display text,
     * not used whilst editing.
     *
     * @param option option to bind controller to
     * @param min minimum allowed value (clamped on apply)
     * @param max maximum allowed value (clamped on apply)
     */
    public IntegerFieldController(Option<Integer> option, int min, int max) {
        this(option, min, max, IntegerSliderController.DEFAULT_FORMATTER);
    }

    /**
     * Constructs a integer field controller.
     * Does not have a minimum or a maximum range.
     *
     * @param option option to bind controller to
     * @param formatter display text, not used whilst editing
     */
    public IntegerFieldController(Option<Integer> option, Function<Integer, Component> formatter) {
        this(option, -Integer.MAX_VALUE, Integer.MAX_VALUE, formatter);
    }

    /**
     * Constructs a integer field controller.
     * Uses {@link IntegerSliderController#DEFAULT_FORMATTER} as display text,
     * not used whilst editing.
     * Does not have a minimum or a maximum range.
     *
     * @param option option to bind controller to
     */
    public IntegerFieldController(Option<Integer> option) {
        this(option, -Integer.MAX_VALUE, Integer.MAX_VALUE, IntegerSliderController.DEFAULT_FORMATTER);
    }

    @Override
    public boolean isInputValid(String input) {
        return input.matches("\\d+|-|");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double min() {
        return this.min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double max() {
        return this.max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString() {
        return String.valueOf(option().pendingValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPendingValue(double value) {
        option().requestSet((int) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double pendingValue() {
        return option().pendingValue();
    }
}
