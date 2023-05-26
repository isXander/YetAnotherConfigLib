package dev.isxander.yacl.impl.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class IntegerSliderControllerBuilderImpl extends AbstractControllerBuilderImpl<Integer> implements IntegerSliderControllerBuilder {
    private int min, max;
    private int step;
    private Function<Integer, Component> formatter = IntegerSliderController.DEFAULT_FORMATTER;

    public IntegerSliderControllerBuilderImpl(Option<Integer> option) {
        super(option);
    }

    @Override
    public IntegerSliderControllerBuilder range(Integer min, Integer max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public IntegerSliderControllerBuilder step(Integer step) {
        this.step = step;
        return this;
    }

    @Override
    public IntegerSliderControllerBuilder valueFormatter(Function<Integer, Component> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Integer> build() {
        return new IntegerSliderController(option, min, max, step, formatter);
    }
}
