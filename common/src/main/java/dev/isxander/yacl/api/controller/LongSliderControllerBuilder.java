package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.LongSliderControllerBuilderImpl;

public interface LongSliderControllerBuilder extends SliderControllerBuilder<Long, LongSliderControllerBuilder> {
    static LongSliderControllerBuilder create(Option<Long> option) {
        return new LongSliderControllerBuilderImpl(option);
    }
}
