package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.IntegerFieldController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class IntegerFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Integer> implements IntegerFieldControllerBuilder {
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;
    private ValueFormatter<Integer> formatter = IntegerSliderController.DEFAULT_FORMATTER::apply;

    public IntegerFieldControllerBuilderImpl(Option<Integer> option) {
        super(option);
    }

    @Override
    public IntegerFieldControllerBuilder min(Integer min) {
        this.min = min;
        return this;
    }

    @Override
    public IntegerFieldControllerBuilder max(Integer max) {
        this.max = max;
        return this;
    }

    @Override
    public IntegerFieldControllerBuilder range(Integer min, Integer max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public IntegerFieldControllerBuilder formatValue(ValueFormatter<Integer> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Integer> build() {
        return IntegerFieldController.createInternal(option, min, max, formatter);
    }
}
