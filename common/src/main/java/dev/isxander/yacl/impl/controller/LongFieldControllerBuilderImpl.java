package dev.isxander.yacl.impl.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.LongFieldControllerBuilder;
import dev.isxander.yacl.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl.gui.controllers.string.number.LongFieldController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class LongFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Long> implements LongFieldControllerBuilder {
    private long min = Long.MIN_VALUE;
    private long max = Long.MAX_VALUE;
    private Function<Long, Component> formatter = LongSliderController.DEFAULT_FORMATTER;

    public LongFieldControllerBuilderImpl(Option<Long> option) {
        super(option);
    }

    @Override
    public LongFieldControllerBuilder min(Long min) {
        this.min = min;
        return this;
    }

    @Override
    public LongFieldControllerBuilder max(Long max) {
        this.max = max;
        return this;
    }

    @Override
    public LongFieldControllerBuilder range(Long min, Long max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public LongFieldControllerBuilder valueFormatter(Function<Long, Component> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Long> build() {
        return new LongFieldController(option, min, max, formatter);
    }
}
