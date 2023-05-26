package dev.isxander.yacl.impl.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl.gui.controllers.TickBoxController;

public class TickBoxControllerBuilderImpl extends AbstractControllerBuilderImpl<Boolean> implements TickBoxControllerBuilder {
    public TickBoxControllerBuilderImpl(Option<Boolean> option) {
        super(option);
    }

    @Override
    public Controller<Boolean> build() {
        return new TickBoxController(option);
    }
}
