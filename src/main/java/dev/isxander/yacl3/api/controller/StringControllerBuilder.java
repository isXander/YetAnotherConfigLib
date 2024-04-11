package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.StringControllerBuilderImpl;

public interface StringControllerBuilder extends ControllerBuilder<String> {
    static StringControllerBuilder create(Option<String> option) {
        return new StringControllerBuilderImpl(option);
    }
}
