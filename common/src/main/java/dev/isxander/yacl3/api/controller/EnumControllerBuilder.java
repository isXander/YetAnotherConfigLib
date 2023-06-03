package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.EnumControllerBuilderImpl;

public interface EnumControllerBuilder<T extends Enum<T>> extends ValueFormattableController<T, EnumControllerBuilder<T>> {
    EnumControllerBuilder<T> enumClass(Class<T> enumClass);

    static <T extends Enum<T>> EnumControllerBuilder<T> create(Option<T> option) {
        return new EnumControllerBuilderImpl<>(option);
    }
}
