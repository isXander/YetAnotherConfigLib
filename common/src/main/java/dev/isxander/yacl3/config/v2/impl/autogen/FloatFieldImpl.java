package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.FloatField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionStorage;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class FloatFieldImpl extends SimpleOptionFactory<FloatField, Float> {
    @Override
    protected ControllerBuilder<Float> createController(FloatField annotation, ConfigField<Float> field, OptionStorage storage, Option<Float> option) {
        return FloatFieldControllerBuilder.create(option)
                .valueFormatter(v -> {
                    String key = null;
                    if (v == annotation.min())
                        key = getTranslationKey(field, "fmt.min");
                    else if (v == annotation.max())
                        key = getTranslationKey(field, "fmt.max");
                    if (key != null && Language.getInstance().has(key))
                        return Component.translatable(key);
                    return Component.translatable(String.format(annotation.format(), v));
                })
                .range(annotation.min(), annotation.max());
    }
}
