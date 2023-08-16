package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Controller;
import org.jetbrains.annotations.ApiStatus;

@FunctionalInterface
public interface ControllerBuilder<T> {
    @ApiStatus.Internal
    Controller<T> build();
}
