package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder;
import dev.isxander.yacl3.gui.controllers.dropdown.EnumDropdownController;

public class EnumDropdownControllerBuilderImpl<E extends Enum<E>> extends AbstractControllerBuilderImpl<E> implements EnumDropdownControllerBuilder<E> {
    public EnumDropdownControllerBuilderImpl(Option<E> option) {
        super(option);
    }

    @Override
    public Controller<E> build() {
        return new EnumDropdownController<>(option);
    }
}
