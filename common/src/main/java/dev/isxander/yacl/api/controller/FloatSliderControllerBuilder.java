package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.FloatSliderControllerBuilderImpl;

public interface FloatSliderControllerBuilder extends SliderControllerBuilder<Float, FloatSliderControllerBuilder> {
    static FloatSliderControllerBuilder create(Option<Float> option) {
        return new FloatSliderControllerBuilderImpl(option);
    }
}
