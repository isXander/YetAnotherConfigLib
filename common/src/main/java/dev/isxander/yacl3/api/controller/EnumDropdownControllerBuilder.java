package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.EnumDropdownControllerBuilderImpl;

import java.util.function.Function;

public interface EnumDropdownControllerBuilder<E extends Enum<E>> extends ValueFormattableController<E, EnumDropdownControllerBuilder<E>> {
    static <E extends Enum<E>> EnumDropdownControllerBuilder<E> create(Option<E> option) {
        return new EnumDropdownControllerBuilderImpl<>(option);
    }

    /**
     * Creates a factory for {@link EnumDropdownControllerBuilder}s with the given function for converting enum constants to components.
     * Use this if a custom formatter function for an enum is needed.
     * Use it like this:
     * <pre>{@code Option.<MyEnum>createBuilder().controller(EnumDropdownControllerBuilder.getFactory(MY_CUSTOM_ENUM_TO_COMPONENT_FUNCTION))}</pre>
     * @param formatter The function used to convert enum constants to components used for display, suggestion, and validation
     * @return a factory for {@link EnumDropdownControllerBuilder}s
     * @param <E> the enum type
     */
    static <E extends Enum<E>> Function<Option<E>, ControllerBuilder<E>> getFactory(ValueFormatter<E> formatter) {
        return opt -> EnumDropdownControllerBuilder.create(opt).formatValue(formatter);
    }
}
