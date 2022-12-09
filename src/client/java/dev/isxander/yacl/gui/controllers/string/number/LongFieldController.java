package dev.isxander.yacl.gui.controllers.string.number;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.gui.controllers.slider.LongSliderController;
import net.minecraft.text.Text;

import java.util.function.Function;

/**
 * {@inheritDoc}
 */
public class LongFieldController extends NumberFieldController<Long> {
    private final long min, max;

    /**
     * Constructs a double field controller
     *
     * @param option option to bind controller to
     * @param min minimum allowed value (clamped on apply)
     * @param max maximum allowed value (clamped on apply)
     * @param formatter display text, not used whilst editing
     */
    public LongFieldController(Option<Long> option, long min, long max, Function<Long, Text> formatter) {
        super(option, formatter);
        this.min = min;
        this.max = max;
    }

    /**
     * Constructs a double field controller.
     * Uses {@link LongSliderController#DEFAULT_FORMATTER} as display text,
     * not used whilst editing.
     *
     * @param option option to bind controller to
     * @param min minimum allowed value (clamped on apply)
     * @param max maximum allowed value (clamped on apply)
     */
    public LongFieldController(Option<Long> option, long min, long max) {
        this(option, min, max, LongSliderController.DEFAULT_FORMATTER);
    }

    /**
     * Constructs a double field controller.
     * Does not have a minimum or a maximum range.
     *
     * @param option option to bind controller to
     * @param formatter display text, not used whilst editing
     */
    public LongFieldController(Option<Long> option, Function<Long, Text> formatter) {
        this(option, -Long.MAX_VALUE, Long.MAX_VALUE, formatter);
    }

    /**
     * Constructs a double field controller.
     * Uses {@link LongSliderController#DEFAULT_FORMATTER} as display text,
     * not used whilst editing.
     * Does not have a minimum or a maximum range.
     *
     * @param option option to bind controller to
     */
    public LongFieldController(Option<Long> option) {
        this(option, -Long.MAX_VALUE, Long.MAX_VALUE, LongSliderController.DEFAULT_FORMATTER);
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
        option().requestSet((long) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double pendingValue() {
        return option().pendingValue();
    }
}
