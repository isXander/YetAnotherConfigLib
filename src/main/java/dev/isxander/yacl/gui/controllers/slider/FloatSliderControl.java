package dev.isxander.yacl.gui.controllers.slider;

import dev.isxander.yacl.api.Option;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;

import java.util.function.Function;

public class FloatSliderControl implements ISliderControl<Float> {
    public static final Function<Float, Text> DEFAULT_FORMATTER = value -> Text.of(String.format("%.1f", value));

    private final Option<Float> option;

    private final float min, max, interval;

    private final Function<Float, Text> valueFormatter;

    public FloatSliderControl(Option<Float> option, float min, float max, float interval) {
        this(option, min, max, interval, DEFAULT_FORMATTER);
    }

    public FloatSliderControl(Option<Float> option, float min, float max, float interval, Function<Float, Text> valueFormatter) {
        Validate.isTrue(max > min, "`max` cannot be smaller than `min`");
        Validate.isTrue(interval > 0, "`interval` must be more than 0");

        this.option = option;
        this.min = min;
        this.max = max;
        this.interval = interval;
        this.valueFormatter = valueFormatter;
    }

    @Override
    public Option<Float> option() {
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
        option().requestSet((float) value);
    }

    @Override
    public double pendingValue() {
        return option().pendingValue();
    }

    @Override
    public Text getValueText(double value) {
        return valueFormatter.apply((float) value);
    }
}
