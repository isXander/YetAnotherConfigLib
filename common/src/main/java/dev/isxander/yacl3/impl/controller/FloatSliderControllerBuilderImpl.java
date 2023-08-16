package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class FloatSliderControllerBuilderImpl extends AbstractControllerBuilderImpl<Float> implements FloatSliderControllerBuilder {
    private float min, max;
    private float step;
    private ValueFormatter<Float> formatter = FloatSliderController.DEFAULT_FORMATTER::apply;

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
    public FloatSliderControllerBuilder formatValue(ValueFormatter<Float> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Float> build() {
        return FloatSliderController.createInternal(option, min, max, step, formatter);
    }
}
