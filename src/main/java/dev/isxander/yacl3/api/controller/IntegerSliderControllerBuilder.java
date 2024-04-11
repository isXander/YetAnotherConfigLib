package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.IntegerSliderControllerBuilderImpl;

public interface IntegerSliderControllerBuilder extends SliderControllerBuilder<Integer, IntegerSliderControllerBuilder> {
    static IntegerSliderControllerBuilder create(Option<Integer> option) {
        return new IntegerSliderControllerBuilderImpl(option);
    }
}
