package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;

public class TickBoxImpl extends SimpleOptionFactory<TickBox, Boolean> {
    @Override
    protected ControllerBuilder<Boolean> createController(TickBox annotation, ConfigField<Boolean> field, OptionAccess storage, Option<Boolean> option) {
        return TickBoxControllerBuilder.create(option);
    }
}
