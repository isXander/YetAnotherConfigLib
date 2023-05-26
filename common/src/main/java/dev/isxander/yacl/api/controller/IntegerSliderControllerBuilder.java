package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.IntegerSliderControllerBuilderImpl;

public interface IntegerSliderControllerBuilder extends SliderControllerBuilder<Integer, IntegerSliderControllerBuilder> {
    static IntegerSliderControllerBuilder create(Option<Integer> option) {
        return new IntegerSliderControllerBuilderImpl(option);
    }
}
