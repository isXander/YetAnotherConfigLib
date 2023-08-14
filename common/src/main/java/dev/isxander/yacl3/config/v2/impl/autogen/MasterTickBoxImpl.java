package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.SimpleOptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.MasterTickBox;
import dev.isxander.yacl3.config.v2.api.autogen.OptionStorage;

public class MasterTickBoxImpl extends SimpleOptionFactory<MasterTickBox, Boolean> {
    @Override
    protected ControllerBuilder<Boolean> createController(MasterTickBox annotation, ConfigField<Boolean> field, OptionStorage storage, Option<Boolean> option) {
        return TickBoxControllerBuilder.create(option);
    }

    @Override
    protected void listener(MasterTickBox annotation, ConfigField<Boolean> field, OptionStorage storage, Option<Boolean> option, Boolean value) {
        for (String child : annotation.value()) {
            storage.scheduleOptionOperation(child, childOpt -> {
                childOpt.setAvailable(annotation.invert() != value);
            });
        }
    }
}
