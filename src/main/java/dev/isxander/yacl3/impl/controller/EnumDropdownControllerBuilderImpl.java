package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.isxander.yacl3.gui.controllers.dropdown.EnumDropdownController;

public class EnumDropdownControllerBuilderImpl<E extends Enum<E>> extends AbstractControllerBuilderImpl<E> implements EnumDropdownControllerBuilder<E> {
    private ValueFormatter<E> formatter = EnumController.<E>getDefaultFormatter()::apply;

    public EnumDropdownControllerBuilderImpl(Option<E> option) {
        super(option);
    }

    @Override
    public EnumDropdownControllerBuilder<E> formatValue(ValueFormatter<E> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<E> build() {
        return new EnumDropdownController<>(option, formatter);
    }
}
