package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.FloatFieldControllerBuilderImpl;

public interface FloatFieldControllerBuilder extends NumberFieldControllerBuilder<Float, FloatFieldControllerBuilder> {
    static FloatFieldControllerBuilder create(Option<Float> option) {
        return new FloatFieldControllerBuilderImpl(option);
    }
}
