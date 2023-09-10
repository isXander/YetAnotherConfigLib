package dev.isxander.yacl3.gui.controllers.string.number;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.slider.ISliderController;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Controller that allows you to enter in numbers using a text field.
 *
 * @param <T> number type
 */
public abstract class NumberFieldController<T extends Number> implements ISliderController<T>, IStringController<T> {

    protected static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private static final List<Character> ALLOWED_CHARS = Util.make(() -> {
        List<Character> chars = new ArrayList<>();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        chars.add(symbols.getDecimalSeparator());
        chars.add(symbols.getMinusSign());
        chars.add(symbols.getZeroDigit());
        chars.add(symbols.getGroupingSeparator());
        symbols.getExponentSeparator().chars().mapToObj(i -> (char) i).forEach(chars::add);
        "123456789".chars().mapToObj(i -> (char) i).forEach(chars::add);
        return chars;
    });

    private final Option<T> option;
    private final ValueFormatter<T> displayFormatter;

    public NumberFieldController(Option<T> option, Function<T, Component> displayFormatter) {
        this.option = option;
        this.displayFormatter = displayFormatter::apply;
    }

    @Override
    public Option<T> option() {
        return this.option;
    }

    @Override
    public void setFromString(String value) {
        try {
            setPendingValue(Mth.clamp(NUMBER_FORMAT.parse(value).doubleValue(), min(), max()));
        } catch (ParseException ignore) {
            YACLConstants.LOGGER.warn("Failed to parse number: {}", value);
        }
    }

    @Override
    public double pendingValue() {
        return option().pendingValue().doubleValue();
    }

    @Override
    public boolean isInputValid(String input) {
        return input.chars().allMatch(i -> ALLOWED_CHARS.contains((char) i));
    }

    @Override
    public Component formatValue() {
        return displayFormatter.format(option().pendingValue());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new StringControllerElement(this, screen, widgetDimension, false);
    }

    @Override
    public double interval() {
        return -1;
    }
}
