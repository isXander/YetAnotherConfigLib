package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionFlag;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.impl.FieldBackedBinding;
import dev.isxander.yacl3.config.v2.impl.autogen.AutoGenUtils;
import dev.isxander.yacl3.config.v2.impl.autogen.EmptyCustomImageFactory;
import dev.isxander.yacl3.config.v2.impl.autogen.YACLAutoGenException;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

public abstract class SimpleOptionFactory<A extends Annotation, T> implements OptionFactory<A, T> {
    @Override
    public Option<T> createOption(A annotation, ConfigField<T> field, OptionAccess optionAccess) {
        Option<T> option = Option.<T>createBuilder()
                .name(this.name(annotation, field, optionAccess))
                .description(v -> this.description(v, annotation, field, optionAccess).build())
                .binding(new FieldBackedBinding<>(field.access(), field.defaultAccess()))
                .controller(opt -> {
                    ControllerBuilder<T> builder = this.createController(annotation, field, optionAccess, opt);

                    AutoGenUtils.addCustomFormatterToController(builder, field.access());

                    return builder;
                })
                .available(this.available(annotation, field, optionAccess))
                .flags(this.flags(annotation, field, optionAccess))
                .listener((opt, v) -> this.listener(annotation, field, optionAccess, opt, v))
                .build();

        postInit(annotation, field, optionAccess, option);
        return option;
    }

    protected abstract ControllerBuilder<T> createController(A annotation, ConfigField<T> field, OptionAccess storage, Option<T> option);

    protected MutableComponent name(A annotation, ConfigField<T> field, OptionAccess storage) {
        Optional<CustomName> customName = field.access().getAnnotation(CustomName.class);
        return Component.translatable(customName.map(CustomName::value).orElse(this.getTranslationKey(field, null)));
    }

    protected OptionDescription.Builder description(T value, A annotation, ConfigField<T> field, OptionAccess storage) {
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

        field.access().getAnnotation(CustomDescription.class).ifPresent(customDescription -> {
            for (String line : customDescription.value()) {
                builder.text(Component.translatable(line));
            }
        });

        Optional<CustomImage> imageOverrideOpt = field.access().getAnnotation(CustomImage.class);
        if (imageOverrideOpt.isPresent()) {
            CustomImage imageOverride = imageOverrideOpt.get();

            if (!imageOverride.factory().equals(EmptyCustomImageFactory.class)) {
                CustomImage.CustomImageFactory<T> imageFactory;
                try {
                    imageFactory = (CustomImage.CustomImageFactory<T>) AutoGenUtils.constructNoArgsClass(
                            imageOverride.factory(),
                            () -> "'%s': The factory class on @OverrideImage has no no-args constructor.".formatted(field.access().name()),
                            () -> "'%s': Failed to instantiate factory class %s.".formatted(field.access().name(), imageOverride.factory().getName())
                    );
                } catch (ClassCastException e) {
                    throw new YACLAutoGenException("'%s': The factory class on @OverrideImage is of incorrect type. Expected %s, got %s.".formatted(field.access().name(), field.access().type().getTypeName(), imageOverride.factory().getTypeParameters()[0].getName()));
                }

                builder.customImage(imageFactory.createImage(value, field, storage).thenApply(Optional::of));
            } else if (!imageOverride.value().isEmpty()) {
                String path = imageOverride.value();
                ResourceLocation imageLocation = new ResourceLocation(field.parent().id().getNamespace(), path);
                String extension = path.substring(path.lastIndexOf('.') + 1);

                switch (extension) {
                    case "png", "jpg", "jpeg" -> builder.image(imageLocation, imageOverride.width(), imageOverride.height());
                    case "webp" -> builder.webpImage(imageLocation);
                    case "gif" -> builder.gifImage(imageLocation);
                    default -> throw new YACLAutoGenException("'%s': Invalid image extension '%s' on @OverrideImage. Expected: ('png','jpg','webp','gif')".formatted(field.access().name(), extension));
                }
            } else {
                throw new YACLAutoGenException("'%s': @OverrideImage has no value or factory class.".formatted(field.access().name()));
            }
        } else {
            String imagePath = "textures/yacl3/" + field.parent().id().getPath() + "/" + field.access().name() + ".webp";
            imagePath = imagePath.toLowerCase().replaceAll("[^a-z0-9/._:-]", "_");
            ResourceLocation imageLocation = new ResourceLocation(field.parent().id().getNamespace(), imagePath);
            if (Minecraft.getInstance().getResourceManager().getResource(imageLocation).isPresent()) {
                builder.webpImage(imageLocation);
            }
        }

        return builder;
    }

    protected boolean available(A annotation, ConfigField<T> field, OptionAccess storage) {
        return true;
    }

    protected Set<OptionFlag> flags(A annotation, ConfigField<T> field, OptionAccess storage) {
        return Set.of();
    }

    protected void listener(A annotation, ConfigField<T> field, OptionAccess storage, Option<T> option, T value) {

    }

    protected void postInit(A annotation, ConfigField<T> field, OptionAccess storage, Option<T> option) {

    }

    protected String getTranslationKey(ConfigField<T> field, @Nullable String suffix) {
        String key = "yacl3.config.%s.%s".formatted(field.parent().id().toString(), field.access().name());
        if (suffix != null) key += "." + suffix;
        return key;
    }
}
