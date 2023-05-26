package dev.isxander.yacl.api.controller;

public interface NumberFieldControllerBuilder<T extends Number, B extends NumberFieldControllerBuilder<T, B>> extends ValueFormattableController<T, B> {
    B min(T min);
    B max(T max);
    B range(T min, T max);
}
