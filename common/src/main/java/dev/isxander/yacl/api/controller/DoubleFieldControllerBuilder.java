package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.DoubleFieldControllerBuilderImpl;

public interface DoubleFieldControllerBuilder extends NumberFieldControllerBuilder<Double, DoubleFieldControllerBuilder> {
    static DoubleFieldControllerBuilder create(Option<Double> option) {
        return new DoubleFieldControllerBuilderImpl(option);
    }
}
