package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.gui.controllers.TickBoxController;

public class TickBoxControllerBuilderImpl extends AbstractControllerBuilderImpl<Boolean> implements TickBoxControllerBuilder {
    public TickBoxControllerBuilderImpl(Option<Boolean> option) {
        super(option);
    }

    @Override
    public Controller<Boolean> build() {
        return new TickBoxController(option);
    }
}
