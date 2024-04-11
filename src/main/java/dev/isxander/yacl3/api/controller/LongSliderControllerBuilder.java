package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.LongSliderControllerBuilderImpl;

public interface LongSliderControllerBuilder extends SliderControllerBuilder<Long, LongSliderControllerBuilder> {
    static LongSliderControllerBuilder create(Option<Long> option) {
        return new LongSliderControllerBuilderImpl(option);
    }
}
