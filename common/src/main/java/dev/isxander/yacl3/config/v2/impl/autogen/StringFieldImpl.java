package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.OptionStorage;
import dev.isxander.yacl3.config.v2.api.autogen.StringField;

public class StringFieldImpl extends SimpleOptionFactory<StringField, String> {
    @Override
    protected ControllerBuilder<String> createController(StringField annotation, ConfigField<String> field, OptionStorage storage, Option<String> option) {
        return StringControllerBuilder.create(option);
    }
}
