package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class DoubleSliderControllerBuilderImpl extends AbstractControllerBuilderImpl<Double> implements DoubleSliderControllerBuilder {
    private double min, max;
    private double step;
    private Function<Double, Component> formatter = DoubleSliderController.DEFAULT_FORMATTER;

    public DoubleSliderControllerBuilderImpl(Option<Double> option) {
        super(option);
    }

    @Override
    public DoubleSliderControllerBuilder range(Double min, Double max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public DoubleSliderControllerBuilder step(Double step) {
        this.step = step;
        return this;
    }

    @Override
    public DoubleSliderControllerBuilder valueFormatter(Function<Double, Component> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Double> build() {
        return new DoubleSliderController(option, min, max, step, formatter);
    }
}
