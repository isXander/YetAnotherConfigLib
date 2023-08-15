package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.LongSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.LongField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionStorage;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class LongFieldImpl extends SimpleOptionFactory<LongField, Long> {
    @Override
    protected ControllerBuilder<Long> createController(LongField annotation, ConfigField<Long> field, OptionStorage storage, Option<Long> option) {
        return LongSliderControllerBuilder.create(option)
                .valueFormatter(v -> {
                    String key = getTranslationKey(field, "fmt." + v);
                    if (Language.getInstance().has(key))
                        return Component.translatable(key);
                    return Component.literal(Long.toString(v));
                })
                .range(annotation.min(), annotation.max());
    }
}
