package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.SimpleOptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.DoubleSlider;
import dev.isxander.yacl3.config.v2.api.autogen.OptionStorage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public class DoubleSliderImpl extends SimpleOptionFactory<DoubleSlider, Double> {
    @Override
    protected ControllerBuilder<Double> createController(DoubleSlider annotation, ConfigField<Double> field, OptionStorage storage, Option<Double> option) {
        return DoubleSliderControllerBuilder.create(option)
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
                .range(annotation.min(), annotation.max())
                .step(annotation.step());
    }
}
