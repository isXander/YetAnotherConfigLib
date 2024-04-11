package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.DoubleFieldController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class DoubleFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Double> implements DoubleFieldControllerBuilder {
    private double min = Double.MIN_VALUE;
    private double max = Double.MAX_VALUE;
    private ValueFormatter<Double> formatter = DoubleSliderController.DEFAULT_FORMATTER::apply;

    public DoubleFieldControllerBuilderImpl(Option<Double> option) {
        super(option);
    }

    @Override
    public DoubleFieldControllerBuilder min(Double min) {
        this.min = min;
        return this;
    }

    @Override
    public DoubleFieldControllerBuilder max(Double max) {
        this.max = max;
        return this;
    }

    @Override
    public DoubleFieldControllerBuilder range(Double min, Double max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public DoubleFieldControllerBuilder formatValue(ValueFormatter<Double> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Double> build() {
        return DoubleFieldController.createInternal(option, min, max, formatter);
    }
}
