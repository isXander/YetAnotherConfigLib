package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.CyclingListControllerBuilderImpl;

public interface CyclingListControllerBuilder<T> extends ValueFormattableController<T, CyclingListControllerBuilder<T>> {
    @SuppressWarnings("unchecked")
    CyclingListControllerBuilder<T> values(T... values);

    CyclingListControllerBuilder<T> values(Iterable<? extends T> values);

    static <T> CyclingListControllerBuilder<T> create(Option<T> option) {
        return new CyclingListControllerBuilderImpl<>(option);
    }
}
