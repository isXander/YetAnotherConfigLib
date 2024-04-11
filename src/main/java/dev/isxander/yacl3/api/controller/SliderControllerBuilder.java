package dev.isxander.yacl3.api.controller;

public interface SliderControllerBuilder<T extends Number, B extends SliderControllerBuilder<T, B>> extends ValueFormattableController<T, B> {
    B range(T min, T max);
    B step(T step);
}
