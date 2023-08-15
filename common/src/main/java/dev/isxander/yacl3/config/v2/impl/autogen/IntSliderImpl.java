package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.IntSlider;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class IntSliderImpl extends SimpleOptionFactory<IntSlider, Integer> {
    @Override
    protected ControllerBuilder<Integer> createController(IntSlider annotation, ConfigField<Integer> field, OptionAccess storage, Option<Integer> option) {
        return IntegerSliderControllerBuilder.create(option)
                .valueFormatter(v -> {
                    String key = getTranslationKey(field, "fmt." + v);
                    if (Language.getInstance().has(key))
                        return Component.translatable(key);
                    return Component.literal(Integer.toString(v));
                })
                .range(annotation.min(), annotation.max())
                .step(annotation.step());
    }
}
