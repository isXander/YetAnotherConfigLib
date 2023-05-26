package dev.isxander.yacl.impl.controller;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.controller.ColorControllerBuilder;
import dev.isxander.yacl.gui.controllers.ColorController;

import java.awt.Color;

public class ColorControllerBuilderImpl extends AbstractControllerBuilderImpl<Color> implements ColorControllerBuilder {
    private boolean allowAlpha = false;

    public ColorControllerBuilderImpl(Option<Color> option) {
        super(option);
    }

    @Override
    public ColorControllerBuilder allowAlpha(boolean allowAlpha) {
        this.allowAlpha = allowAlpha;
        return this;
    }

    @Override
    public Controller<Color> build() {
        return new ColorController(option, allowAlpha);
    }
}
