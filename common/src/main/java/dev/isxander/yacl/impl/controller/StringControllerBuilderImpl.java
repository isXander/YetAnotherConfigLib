package dev.isxander.yacl.impl.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.StringControllerBuilder;
import dev.isxander.yacl.gui.controllers.string.StringController;

public class StringControllerBuilderImpl extends AbstractControllerBuilderImpl<String> implements StringControllerBuilder {
    public StringControllerBuilderImpl(Option<String> option) {
        super(option);
    }

    @Override
    public Controller<String> build() {
        return new StringController(option);
    }
}
