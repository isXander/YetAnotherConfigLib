package dev.isxander.yacl.gui.controllers.string.number;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.slider.ISliderController;
import dev.isxander.yacl.gui.controllers.string.IStringController;
import dev.isxander.yacl.gui.controllers.string.StringControllerElement;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.text.DecimalFormatSymbols;
import java.util.function.Function;

/**
 * Controller that allows you to enter in numbers using a text field.
 *
 * @param <T> number type
 */
public abstract class NumberFieldController<T extends Number> implements ISliderController<T>, IStringController<T> {
    private final Option<T> option;
    private final Function<T, Component> displayFormatter;

    public NumberFieldController(Option<T> option, Function<T, Component> displayFormatter) {
        this.option = option;
        this.displayFormatter = displayFormatter;
    }

    @Override
    public Option<T> option() {
        return this.option;
    }

    @Override
    public void setFromString(String value) {
        if (value.isEmpty() || value.equals(".") || value.equals("-")) value = "0";
        setPendingValue(Mth.clamp(Double.parseDouble(cleanupNumberString(value)), min(), max()));
    }

    @Override
    public double pendingValue() {
        return option().pendingValue().doubleValue();
    }

    @Override
    public boolean isInputValid(String input) {
        return input.matches("[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)|[.]||-");
    }

    @Override
    public Component formatValue() {
        return displayFormatter.apply(option().pendingValue());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new StringControllerElement(this, screen, widgetDimension, false);
    }

    protected String cleanupNumberString(String number) {
        return number.replace(String.valueOf(DecimalFormatSymbols.getInstance().getGroupingSeparator()), "");
    }

    @Override
    public double interval() {
        return -1;
    }
}
