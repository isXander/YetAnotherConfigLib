package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.EnumDropdownControllerBuilderImpl;

public interface EnumDropdownControllerBuilder<E extends Enum<E>> extends ControllerBuilder<E> {
    static <E extends Enum<E>> EnumDropdownControllerBuilder<E> create(Option<E> option) {
        return new EnumDropdownControllerBuilderImpl<>(option);
    }
}
