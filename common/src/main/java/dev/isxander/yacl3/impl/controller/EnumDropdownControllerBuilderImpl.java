package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder;
import dev.isxander.yacl3.gui.controllers.dropdown.EnumDropdownController;

import java.util.function.Function;

public class EnumDropdownControllerBuilderImpl<E extends Enum<E>> extends AbstractControllerBuilderImpl<E> implements EnumDropdownControllerBuilder<E> {
    private Function<E, String> toString = Enum::toString;

    public EnumDropdownControllerBuilderImpl(Option<E> option) {
        super(option);
    }

    @Override
    public EnumDropdownControllerBuilder<E> toString(Function<E, String> toString) {
        this.toString = toString;
        return this;
    }

    @Override
    public Controller<E> build() {
        return new EnumDropdownController<>(option, toString);
    }
}
