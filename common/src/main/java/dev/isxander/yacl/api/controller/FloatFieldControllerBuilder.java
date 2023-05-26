package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.FloatFieldControllerBuilderImpl;

public interface FloatFieldControllerBuilder extends NumberFieldControllerBuilder<Float, FloatFieldControllerBuilder> {
    static FloatFieldControllerBuilder create(Option<Float> option) {
        return new FloatFieldControllerBuilderImpl(option);
    }
}
