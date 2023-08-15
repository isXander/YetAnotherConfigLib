package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.ColorRGBA;
import dev.isxander.yacl3.config.v2.api.autogen.OptionStorage;

import java.awt.Color;

public class ColorRGBAImpl extends SimpleOptionFactory<ColorRGBA, Color> {
    @Override
    protected ControllerBuilder<Color> createController(ColorRGBA annotation, ConfigField<Color> field, OptionStorage storage, Option<Color> option) {
        return ColorControllerBuilder.create(option)
                .allowAlpha(annotation.allowAlpha());
    }
}
