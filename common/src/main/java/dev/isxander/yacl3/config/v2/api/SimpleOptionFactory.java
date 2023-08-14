package dev.isxander.yacl3.config.v2.api;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.autogen.OptionStorage;
import dev.isxander.yacl3.config.v2.impl.FieldBackedBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Set;

public abstract class SimpleOptionFactory<A extends Annotation, T> implements OptionFactory<A, T> {
    @Override
    public Option<T> createOption(A annotation, ConfigField<T> field, OptionStorage storage) {
        Option<T> option = Option.<T>createBuilder()
                .name(this.name(annotation, field, storage))
                .description(v -> this.description(v, annotation, field, storage).build())
                .binding(new FieldBackedBinding<>(field.access(), field.defaultAccess()))
                .controller(opt -> this.createController(annotation, field, storage, opt))
                .available(this.available(annotation, field, storage))
                .flags(this.flags(annotation, field, storage))
                .listener((opt, v) -> this.listener(annotation, field, storage, opt, v))
                .build();

        postInit(annotation, field, storage, option);
        return option;
    }

    protected abstract ControllerBuilder<T> createController(A annotation, ConfigField<T> field, OptionStorage storage, Option<T> option);

    protected MutableComponent name(A annotation, ConfigField<T> field, OptionStorage storage) {
        return Component.translatable(this.getTranslationKey(field, null));
    }

    protected OptionDescription.Builder description(T value, A annotation, ConfigField<T> field, OptionStorage storage) {
        OptionDescription.Builder builder = OptionDescription.createBuilder();

        String key = this.getTranslationKey(field, "desc");
        if (Language.getInstance().has(key)) {
            builder.text(Component.translatable(key));
        } else {
            key += ".";
            int i = 0;
            while (Language.getInstance().has(key + i++)) {
                builder.text(Component.translatable(key + i));
            }
        }

        String imagePath = "textures/yacl3/" + field.parent().id().getPath() + "/" + field.access().name() + ".webp";
        imagePath = imagePath.toLowerCase().replaceAll("[^a-z0-9/._:-]", "_");
        ResourceLocation imageLocation = new ResourceLocation(field.parent().id().getNamespace(), imagePath);
        if (Minecraft.getInstance().getResourceManager().getResource(imageLocation).isPresent()) {
            builder.webpImage(imageLocation);
        }

        return builder;
    }

    protected boolean available(A annotation, ConfigField<T> field, OptionStorage storage) {
        return true;
    }

    protected Set<OptionFlag> flags(A annotation, ConfigField<T> field, OptionStorage storage) {
        return Set.of();
    }

    protected void listener(A annotation, ConfigField<T> field, OptionStorage storage, Option<T> option, T value) {

    }

    protected void postInit(A annotation, ConfigField<T> field, OptionStorage storage, Option<T> option) {

    }

    protected String getTranslationKey(ConfigField<T> field, @Nullable String suffix) {
        String key = "yacl3.config.%s.%s".formatted(field.parent().id().toString(), field.access().name());
        if (suffix != null) key += "." + suffix;
        return key;
    }
}
