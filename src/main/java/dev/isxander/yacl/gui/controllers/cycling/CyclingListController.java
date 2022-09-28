package dev.isxander.yacl.gui.controllers.cycling;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.Option;
import net.minecraft.text.Text;

import java.util.function.Function;

public class CyclingListController<T> implements ICyclingController<T> {
    private final Option<T> option;
    private final Function<T, Text> valueFormatter;
    private final ImmutableList<T> values;

    public CyclingListController(Option<T> option, Iterable<T> values) {
        this(option, values, value -> Text.of(value.toString()));
    }

    public CyclingListController(Option<T> option, Iterable<T> values, Function<T, Text> valueFormatter) {
        this.option = option;
        this.valueFormatter = valueFormatter;
        this.values = ImmutableList.copyOf(values);
    }

    @Override
    public Option<T> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return valueFormatter.apply(option().pendingValue());
    }

    @Override
    public void setPendingValue(int ordinal) {
        option().requestSet(values.get(ordinal));
    }

    @Override
    public int getPendingValue() {
        return values.indexOf(option().pendingValue());
    }

    @Override
    public int getCycleLength() {
        return values.size();
    }
}
