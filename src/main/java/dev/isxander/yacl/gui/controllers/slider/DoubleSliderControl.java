package dev.isxander.yacl.gui.controllers.slider;

import dev.isxander.yacl.api.Option;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;

import java.util.function.Function;

public class DoubleSliderControl implements ISliderControl<Double> {
    public static final Function<Double, Text> DEFAULT_FORMATTER = value -> Text.of(String.format("%.2f", value));

    private final Option<Double> option;

    private final double min, max, interval;

    private final Function<Double, Text> valueFormatter;

    public DoubleSliderControl(Option<Double> option, double min, double max, double interval) {
        this(option, min, max, interval, DEFAULT_FORMATTER);
    }

    public DoubleSliderControl(Option<Double> option, double min, double max, double interval, Function<Double, Text> valueFormatter) {
        Validate.isTrue(max > min, "`max` cannot be smaller than `min`");
        Validate.isTrue(interval > 0, "`interval` must be more than 0");

        this.option = option;
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.valueFormatter = valueFormatter;
    }

    @Override
    public Option<Double> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return valueFormatter.apply(option().pendingValue());
    }

    @Override
    public double min() {
        return min;
    }

    @Override
    public double max() {
        return max;
    }

    @Override
    public double interval() {
        return interval;
    }

    @Override
    public void setPendingValue(double value) {
        option().requestSet(value);
    }

    @Override
    public double pendingValue() {
        return option().pendingValue();
    }

    @Override
    public Text getValueText(double value) {
        return valueFormatter.apply(value);
    }
}
