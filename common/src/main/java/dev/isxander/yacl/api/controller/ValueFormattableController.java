package dev.isxander.yacl.api.controller;

import net.minecraft.network.chat.Component;

import java.util.function.Function;

public interface ValueFormattableController<T, B extends ValueFormattableController<T, B>> extends ControllerBuilder<T> {
    B valueFormatter(Function<T, Component> formatter);
}
