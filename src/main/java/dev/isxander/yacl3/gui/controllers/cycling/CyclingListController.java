package dev.isxander.yacl3.gui.controllers.cycling;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * A controller where once clicked, cycles through elements
 * in the provided list.
 */
public class CyclingListController<T> implements ICyclingController<T> {
    private final Option<T> option;
    private final ValueFormatter<T> valueFormatter;
    private final ImmutableList<T> values;

    /**
     * Constructs a {@link CyclingListController}, with a default
     * value formatter of {@link Object#toString()}.
     * @param option option of which to bind the controller to
     * @param values the values to cycle through
     */
    public CyclingListController(Option<T> option, Iterable<? extends T> values) {
        this(option, values, value -> Component.literal(value.toString()));
    }

    /**
     * Constructs a {@link CyclingListController}
     * @param option option of which to bind the controller to
     * @param values the values to cycle through
     * @param valueFormatter function of how to convert each value to a string to display
     */
    public CyclingListController(Option<T> option, Iterable<? extends T> values, Function<T, Component> valueFormatter) {
        this.option = option;
        this.valueFormatter = valueFormatter::apply;
        this.values = ImmutableList.copyOf(values);
    }

    @ApiStatus.Internal
    public static <T> CyclingListController<T> createInternal(Option<T> option, Iterable<? extends T> values, ValueFormatter<T> formatter) {
        return new CyclingListController<>(option, values, formatter::format);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<T> option() {
        return option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component formatValue() {
        return valueFormatter.format(option().pendingValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPendingValue(int ordinal) {
        option().requestSet(values.get(ordinal));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPendingValue() {
        return values.indexOf(option().pendingValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCycleLength() {
        return values.size();
    }
}
