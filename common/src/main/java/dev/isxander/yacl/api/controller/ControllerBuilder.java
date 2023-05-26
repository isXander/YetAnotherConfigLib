package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Controller;
import org.jetbrains.annotations.ApiStatus;

public interface ControllerBuilder<T> {
    @ApiStatus.Internal
    Controller<T> build();
}
