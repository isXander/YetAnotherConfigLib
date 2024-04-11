package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.LongSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.LongSlider;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class LongSliderImpl extends SimpleOptionFactory<LongSlider, Long> {
    @Override
    protected ControllerBuilder<Long> createController(LongSlider annotation, ConfigField<Long> field, OptionAccess storage, Option<Long> option) {
        return LongSliderControllerBuilder.create(option)
                .formatValue(v -> {
                    String key = getTranslationKey(field, "fmt." + v);
                    if (Language.getInstance().has(key))
                        return Component.translatable(key);
                    key = getTranslationKey(field, "fmt");
                    if (Language.getInstance().has(key))
                        return Component.translatable(key, v);
                    return Component.literal(Long.toString(v));
                })
                .range(annotation.min(), annotation.max())
                .step(annotation.step());
    }
}
