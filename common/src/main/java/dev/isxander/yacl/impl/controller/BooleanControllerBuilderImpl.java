package dev.isxander.yacl.impl.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl.gui.controllers.BooleanController;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;

import java.util.function.Function;

public class BooleanControllerBuilderImpl extends AbstractControllerBuilderImpl<Boolean> implements BooleanControllerBuilder {
    private boolean coloured = false;
    private Function<Boolean, Component> formatter = BooleanController.ON_OFF_FORMATTER;

    public BooleanControllerBuilderImpl(Option<Boolean> option) {
        super(option);
    }

    @Override
    public BooleanControllerBuilder coloured(boolean coloured) {
        this.coloured = coloured;
        return this;
    }

    @Override
    public BooleanControllerBuilder valueFormatter(Function<Boolean, Component> formatter) {
        Validate.notNull(formatter);

        this.formatter = formatter;
        return this;
    }

    @Override
    public BooleanControllerBuilder onOffFormatter() {
        this.formatter = BooleanController.ON_OFF_FORMATTER;
        return this;
    }

    @Override
    public BooleanControllerBuilder yesNoFormatter() {
        this.formatter = BooleanController.YES_NO_FORMATTER;
        return this;
    }

    @Override
    public BooleanControllerBuilder trueFalseFormatter() {
        this.formatter = BooleanController.TRUE_FALSE_FORMATTER;
        return this;
    }

    @Override
    public Controller<Boolean> build() {
        return new BooleanController(option, formatter, coloured);
    }
}
