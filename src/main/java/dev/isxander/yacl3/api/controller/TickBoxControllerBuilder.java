package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.TickBoxControllerBuilderImpl;

public interface TickBoxControllerBuilder extends ControllerBuilder<Boolean> {
    static TickBoxControllerBuilder create(Option<Boolean> option) {
        return new TickBoxControllerBuilderImpl(option);
    }
}
