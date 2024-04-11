package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.DoubleFieldControllerBuilderImpl;

public interface DoubleFieldControllerBuilder extends NumberFieldControllerBuilder<Double, DoubleFieldControllerBuilder> {
    static DoubleFieldControllerBuilder create(Option<Double> option) {
        return new DoubleFieldControllerBuilderImpl(option);
    }
}
