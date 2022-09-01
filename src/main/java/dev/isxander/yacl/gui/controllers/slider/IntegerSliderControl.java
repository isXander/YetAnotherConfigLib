package dev.isxander.yacl.gui.controllers.slider;

import dev.isxander.yacl.api.Option;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;

import java.util.function.Function;

public class IntegerSliderControl implements ISliderControl<Integer> {
    public static final Function<Integer, Text> DEFAULT_FORMATTER = value -> Text.of(String.valueOf(value));

    private final Option<Integer> option;

    private final int min, max, interval;

    private final Function<Integer, Text> valueFormatter;

    public IntegerSliderControl(Option<Integer> option, int min, int max, int interval) {
        this(option, min, max, interval, DEFAULT_FORMATTER);
    }

    public IntegerSliderControl(Option<Integer> option, int min, int max, int interval, Function<Integer, Text> valueFormatter) {
        Validate.isTrue(max > min, "`max` cannot be smaller than `min`");
        Validate.isTrue(interval > 0, "`interval` must be more than 0");

        this.option = option;
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.valueFormatter = valueFormatter;
    }

    @Override
    public Option<Integer> option() {
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
        option().requestSet((int) value);
    }

    @Override
    public double pendingValue() {
        return option().pendingValue();
    }

    @Override
    public Text getValueText(double value) {
        return valueFormatter.apply((int) value);
    }
}
