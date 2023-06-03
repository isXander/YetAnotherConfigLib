package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.DoubleSliderControllerBuilderImpl;

public interface DoubleSliderControllerBuilder extends SliderControllerBuilder<Double, DoubleSliderControllerBuilder> {
    static DoubleSliderControllerBuilder create(Option<Double> option) {
        return new DoubleSliderControllerBuilderImpl(option);
    }
}
