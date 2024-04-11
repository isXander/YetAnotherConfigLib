package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.ColorControllerBuilderImpl;

import java.awt.Color;

public interface ColorControllerBuilder extends ControllerBuilder<Color> {
    ColorControllerBuilder allowAlpha(boolean allowAlpha);

    static ColorControllerBuilder create(Option<Color> option) {
        return new ColorControllerBuilderImpl(option);
    }
}
