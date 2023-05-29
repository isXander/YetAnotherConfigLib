package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.DoubleSliderControllerBuilderImpl;

public interface DoubleSliderControllerBuilder extends SliderControllerBuilder<Double, DoubleSliderControllerBuilder> {
    static DoubleSliderControllerBuilder create(Option<Double> option) {
        return new DoubleSliderControllerBuilderImpl(option);
    }
}
