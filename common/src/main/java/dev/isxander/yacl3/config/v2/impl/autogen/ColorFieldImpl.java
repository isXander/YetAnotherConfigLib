package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.ColorField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;

import java.awt.Color;

public class ColorFieldImpl extends SimpleOptionFactory<ColorField, Color> {
    @Override
    protected ControllerBuilder<Color> createController(ColorField annotation, ConfigField<Color> field, OptionAccess storage, Option<Color> option) {
        return ColorControllerBuilder.create(option)
                .allowAlpha(annotation.allowAlpha());
    }
}
