package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.ColorControllerBuilderImpl;

import java.awt.Color;

public interface ColorControllerBuilder extends ControllerBuilder<Color> {
    ColorControllerBuilder allowAlpha(boolean allowAlpha);

    static ColorControllerBuilder create(Option<Color> option) {
        return new ColorControllerBuilderImpl(option);
    }
}
