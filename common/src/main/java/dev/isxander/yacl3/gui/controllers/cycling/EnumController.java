package dev.isxander.yacl3.gui.controllers.cycling;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import net.minecraft.network.chat.Component;
import net.minecraft.util.OptionEnum;

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
    public static <T extends Enum<T>> Function<T, Component> getDefaultFormatter() {
        return value -> {
            if (value instanceof NameableEnum nameableEnum)
                return nameableEnum.getDisplayName();
            if (value instanceof OptionEnum translatableOption)
                return translatableOption.getCaption();
            return Component.literal(value.toString());
        };
    }

    public EnumController(Option<T> option, Class<T> enumClass) {
        this(option, getDefaultFormatter(), enumClass.getEnumConstants());
    }

    /**
     * Constructs a cycling enum controller.
     *
     * @param option bound option
     * @param valueFormatter format the enum into any {@link Component}
     * @param availableValues all enum constants that can be cycled through
     */
    public EnumController(Option<T> option, Function<T, Component> valueFormatter, T[] availableValues) {
        super(option, Arrays.asList(availableValues), valueFormatter);
    }
}
