package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.StringControllerBuilderImpl;

public interface StringControllerBuilder extends ControllerBuilder<String> {
    static StringControllerBuilder create(Option<String> option) {
        return new StringControllerBuilderImpl(option);
    }
}
