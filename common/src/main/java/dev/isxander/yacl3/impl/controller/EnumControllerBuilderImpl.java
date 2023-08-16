package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class EnumControllerBuilderImpl<T extends Enum<T>> extends AbstractControllerBuilderImpl<T> implements EnumControllerBuilder<T> {
    private Class<T> enumClass;
    private ValueFormatter<T> formatter = null;

    public EnumControllerBuilderImpl(Option<T> option) {
        super(option);
    }

    @Override
    public EnumControllerBuilder<T> enumClass(Class<T> enumClass) {
        this.enumClass = enumClass;
        return this;
    }

    @Override
    public EnumControllerBuilder<T> formatValue(ValueFormatter<T> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<T> build() {
        ValueFormatter<T> formatter = this.formatter;
        if (formatter == null) {
            Function<T, Component> formatFunction = EnumController.getDefaultFormatter();
            formatter = formatFunction::apply;
        }

        return EnumController.createInternal(option, formatter, enumClass.getEnumConstants());
    }
}
