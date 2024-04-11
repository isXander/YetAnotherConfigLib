package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.LongFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl3.gui.controllers.string.number.LongFieldController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class LongFieldControllerBuilderImpl extends AbstractControllerBuilderImpl<Long> implements LongFieldControllerBuilder {
    private long min = Long.MIN_VALUE;
    private long max = Long.MAX_VALUE;
    private ValueFormatter<Long> formatter = LongSliderController.DEFAULT_FORMATTER::apply;

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
    public LongFieldControllerBuilder formatValue(ValueFormatter<Long> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<Long> build() {
        return LongFieldController.createInternal(option, min, max, formatter);
    }
}
