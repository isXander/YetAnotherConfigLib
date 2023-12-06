package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;

public class EnumDropdownController<E extends Enum<E>> extends AbstractDropdownController<E> {
    /**
     * The function used to convert enum constants to strings used for display, suggestion, and validation. Defaults to {@link Enum#toString}.
     */
    protected final ValueFormatter<E> formatter;

    public EnumDropdownController(Option<E> option, ValueFormatter<E> formatter) {
        super(option, Arrays.stream(option.pendingValue().getDeclaringClass().getEnumConstants()).map(formatter::format).map(Component::getString).toList());
        this.formatter = formatter;
    }

    @Override
    public String getString() {
        return formatter.format(option().pendingValue()).getString();
    }

    @Override
    public void setFromString(String value) {
        option().requestSet(getEnumFromString(value));
    }

    /**
     * Searches through enum constants for one whose {@link #formatter} result equals {@code value}
     *
     * @return The enum constant associated with the {@code value} or the pending value if none are found
     * @implNote The return value of {@link #formatter} on each enum constant should be unique in order to ensure accuracy
     */
    private E getEnumFromString(String value) {
        value = value.toLowerCase();
        for (E constant : option().pendingValue().getDeclaringClass().getEnumConstants()) {
            if (formatter.format(constant).getString().toLowerCase().equals(value)) return constant;
        }

        return option().pendingValue();
    }

    @Override
    public boolean isValueValid(String value) {
        value = value.toLowerCase();
        for (String constant : getAllowedValues()) {
            if (constant.equals(value)) return true;
        }

        return false;
    }

    @Override
    protected String getValidValue(String value, int offset) {
        return getValidEnumConstants(value)
                .skip(offset)
                .findFirst()
                .orElseGet(this::getString);
    }

    /**
     * Filters and sorts through enum constants for those whose {@link #formatter} result equals {@code value}
     *
     * @return a sorted stream containing enum constants associated with the {@code value}
     * @implNote The return value of {@link #formatter} on each enum constant should be unique in order to ensure accuracy
     */
    @NotNull
    protected Stream<String> getValidEnumConstants(String value) {
        String valueLowerCase = value.toLowerCase();
        return getAllowedValues().stream()
                .filter(constant -> constant.toLowerCase().contains(valueLowerCase))
                .sorted((s1, s2) -> {
                    String s1LowerCase = s1.toLowerCase();
                    String s2LowerCase = s2.toLowerCase();
                    if (s1LowerCase.startsWith(valueLowerCase) && !s2LowerCase.startsWith(valueLowerCase)) return -1;
                    if (!s1LowerCase.startsWith(valueLowerCase) && s2LowerCase.startsWith(valueLowerCase)) return 1;
                    return s1.compareTo(s2);
                });
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new EnumDropdownControllerElement<>(this, screen, widgetDimension);
    }
}
