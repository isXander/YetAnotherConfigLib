package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class FloatFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Float> implements FloatFieldControllerBuilder {
    private float min = Float.MIN_VALUE;
    private float max = Float.MAX_VALUE;
    private Function<Float, Component> formatter = FloatSliderController.DEFAULT_FORMATTER;

    public FloatFieldControllerBuilderImpl(Option<Float> option) {
        super(option);
    }

    @Override
    public FloatFieldControllerBuilder min(Float min) {
        this.min = min;
        return this;
    }

    @Override
    public FloatFieldControllerBuilder max(Float max) {
        this.max = max;
        return this;
    }

    @Override
    public FloatFieldControllerBuilder range(Float min, Float max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public FloatFieldControllerBuilder valueFormatter(Function<Float, Component> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Float> build() {
        return new FloatFieldController(option, min, max, formatter);
    }
}
