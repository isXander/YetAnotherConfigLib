package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;

public abstract class AbstractControllerBuilderImpl<T> implements ControllerBuilder<T> {
    protected final Option<T> option;

    protected AbstractControllerBuilderImpl(Option<T> option) {
        this.option = option;
    }
}
