package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.TickBoxControllerBuilderImpl;

public interface TickBoxControllerBuilder extends ControllerBuilder<Boolean> {
    static TickBoxControllerBuilder create(Option<Boolean> option) {
        return new TickBoxControllerBuilderImpl(option);
    }
}
