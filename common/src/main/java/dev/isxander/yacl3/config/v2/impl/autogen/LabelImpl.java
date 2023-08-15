package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.Label;
import dev.isxander.yacl3.config.v2.api.autogen.OptionStorage;
import net.minecraft.network.chat.Component;

public class LabelImpl implements OptionFactory<Label, Component> {
    @Override
    public Option<Component> createOption(Label annotation, ConfigField<Component> field, OptionStorage storage) {
        return LabelOption.create(field.access().get());
    }
}
