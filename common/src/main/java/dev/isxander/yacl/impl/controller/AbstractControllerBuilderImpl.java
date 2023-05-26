package dev.isxander.yacl.impl.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.ControllerBuilder;

public abstract class AbstractControllerBuilderImpl<T> implements ControllerBuilder<T> {
    protected final Option<T> option;

    protected AbstractControllerBuilderImpl(Option<T> option) {
        this.option = option;
    }
}
