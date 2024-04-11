package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.FloatSliderControllerBuilderImpl;

public interface FloatSliderControllerBuilder extends SliderControllerBuilder<Float, FloatSliderControllerBuilder> {
    static FloatSliderControllerBuilder create(Option<Float> option) {
        return new FloatSliderControllerBuilderImpl(option);
    }
}
