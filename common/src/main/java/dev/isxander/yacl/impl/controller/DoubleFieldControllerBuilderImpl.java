package dev.isxander.yacl.impl.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.string.number.DoubleFieldController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class DoubleFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Double> implements DoubleFieldControllerBuilder {
    private double min = Double.MIN_VALUE;
    private double max = Double.MAX_VALUE;
    private Function<Double, Component> formatter = DoubleSliderController.DEFAULT_FORMATTER;

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
    public DoubleFieldControllerBuilder valueFormatter(Function<Double, Component> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Double> build() {
        return new DoubleFieldController(option, min, max, formatter);
    }
}
