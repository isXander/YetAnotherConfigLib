package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.LongSliderControllerBuilder;
import dev.isxander.yacl3.gui.controllers.slider.LongSliderController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class LongSliderControllerBuilderImpl extends AbstractControllerBuilderImpl<Long> implements LongSliderControllerBuilder {
    private long min, max;
    private long step;
    private Function<Long, Component> formatter = LongSliderController.DEFAULT_FORMATTER;

    public LongSliderControllerBuilderImpl(Option<Long> option) {
        super(option);
    }

    @Override
    public LongSliderControllerBuilder range(Long min, Long max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public LongSliderControllerBuilder step(Long step) {
        this.step = step;
        return this;
    }

    @Override
    public LongSliderControllerBuilder valueFormatter(Function<Long, Component> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Long> build() {
        return new LongSliderController(option, min, max, step, formatter);
    }
}
