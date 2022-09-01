package dev.isxander.yacl.gui.controllers.slider;

import dev.isxander.yacl.api.Option;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;

import java.util.function.Function;

/**
 * {@link ISliderController} for doubles.
 */
public class DoubleSliderController implements ISliderController<Double> {
    /**
     * Formats doubles to two decimal places
     */
    public static final Function<Double, Text> DEFAULT_FORMATTER = value -> Text.of(String.format("%.2f", value));

    private final Option<Double> option;

    private final double min, max, interval;

    private final Function<Double, Text> valueFormatter;

    /**
     * Constructs a {@link ISliderController} for doubles
     * using the default value formatter {@link DoubleSliderController#DEFAULT_FORMATTER}.
     *
     * @param option bound option
     * @param min minimum slider value
     * @param max maximum slider value
     * @param interval step size (or increments) for the slider
     */
    public DoubleSliderController(Option<Double> option, double min, double max, double interval) {
        this(option, min, max, interval, DEFAULT_FORMATTER);
    }

    /**
     * Constructs a {@link ISliderController} for doubles.
     *
     * @param option bound option
     * @param min minimum slider value
     * @param max maximum slider value
     * @param interval step size (or increments) for the slider
     * @param valueFormatter format the value into any {@link Text}
     */
    public DoubleSliderController(Option<Double> option, double min, double max, double interval, Function<Double, Text> valueFormatter) {
        Validate.isTrue(max > min, "`max` cannot be smaller than `min`");
        Validate.isTrue(interval > 0, "`interval` must be more than 0");

        this.option = option;
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.valueFormatter = valueFormatter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<Double> option() {
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
    public double min() {
        return min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double max() {
        return max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double interval() {
        return interval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPendingValue(double value) {
        option().requestSet(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double pendingValue() {
        return option().pendingValue();
    }

}
