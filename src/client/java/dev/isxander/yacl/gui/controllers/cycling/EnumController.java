package dev.isxander.yacl.gui.controllers.cycling;

import dev.isxander.yacl.api.NameableEnum;
import dev.isxander.yacl.api.Option;
import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Simple controller type that displays the enum on the right.
 * <p>
 * Cycles forward with left click, cycles backward with right click or when shift is held
 *
 * @param <T> enum type
 */
public class EnumController<T extends Enum<T>> extends CyclingListController<T> {
    public static <T extends Enum<T>> Function<T, Text> getDefaultFormatter() {
        return value -> {
            if (value instanceof NameableEnum nameableEnum)
                return nameableEnum.getDisplayName();
            if (value instanceof TranslatableOption translatableOption)
                return translatableOption.getText();
            return Text.of(value.toString());
        };
    }

    /**
     * Constructs a cycling enum controller with a default value formatter and all values being available.
     * The default value formatter first searches if the
     * enum is a {@link NameableEnum} or {@link TranslatableOption} else, just uses {@link Enum#toString()}
     *
     * @param option bound option
     */
    public EnumController(Option<T> option) {
        this(option, getDefaultFormatter());
    }

    /**
     * Constructs a cycling enum controller with all values being available.
     *
     * @param option bound option
     * @param valueFormatter format the enum into any {@link Text}
     */
    public EnumController(Option<T> option, Function<T, Text> valueFormatter) {
        this(option, valueFormatter, option.typeClass().getEnumConstants());
    }

    /**
     * Constructs a cycling enum controller.
     *
     * @param option bound option
     * @param valueFormatter format the enum into any {@link Text}
     * @param availableValues all enum constants that can be cycled through
     */
    public EnumController(Option<T> option, Function<T, Text> valueFormatter, T[] availableValues) {
        super(option, Arrays.asList(availableValues), valueFormatter);
    }
}
