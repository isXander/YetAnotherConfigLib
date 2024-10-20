package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class FloatFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Float> implements FloatFieldControllerBuilder {
    private float min = -Float.MAX_VALUE;
    private float max = Float.MAX_VALUE;
    private ValueFormatter<Float> formatter = FloatSliderController.DEFAULT_FORMATTER::apply;

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
    public FloatFieldControllerBuilder formatValue(ValueFormatter<Float> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Float> build() {
        return FloatFieldController.createInternal(option, min, max, formatter);
    }
}
