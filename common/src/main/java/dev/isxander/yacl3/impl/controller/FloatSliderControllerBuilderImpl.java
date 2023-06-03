package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class FloatSliderControllerBuilderImpl extends AbstractControllerBuilderImpl<Float> implements FloatSliderControllerBuilder {
    private float min, max;
    private float step;
    private Function<Float, Component> formatter = FloatSliderController.DEFAULT_FORMATTER;

    public FloatSliderControllerBuilderImpl(Option<Float> option) {
        super(option);
    }

    @Override
    public FloatSliderControllerBuilder range(Float min, Float max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public FloatSliderControllerBuilder step(Float step) {
        this.step = step;
        return this;
    }

    @Override
    public FloatSliderControllerBuilder valueFormatter(Function<Float, Component> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Float> build() {
        return new FloatSliderController(option, min, max, step, formatter);
    }
}
