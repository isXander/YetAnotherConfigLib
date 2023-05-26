package dev.isxander.yacl.api.controller;

public interface SliderControllerBuilder<T extends Number, B extends SliderControllerBuilder<T, B>> extends ValueFormattableController<T, B> {
    B range(T min, T max);
    B step(T step);
}
