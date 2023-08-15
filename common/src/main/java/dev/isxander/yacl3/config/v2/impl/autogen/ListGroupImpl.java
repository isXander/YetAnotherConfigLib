package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.ListGroup;
import dev.isxander.yacl3.config.v2.api.autogen.OptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.impl.FieldBackedBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ListGroupImpl<T> implements OptionFactory<ListGroup, List<T>> {
    @Override
    public Option<List<T>> createOption(ListGroup annotation, ConfigField<List<T>> field, OptionAccess optionAccess) {
        if (field.autoGen().orElseThrow().group().isPresent()) {
            throw new YACLAutoGenException("@ListGroup fields ('%s') cannot be inside a group as lists act as groups.".formatted(field.access().name()));
        }

        ListGroup.ValueFactory<T> valueFactory = createValueFactory((Class<? extends ListGroup.ValueFactory<T>>) annotation.valueFactory());
        ListGroup.ControllerFactory<T> controllerFactory = createControllerFactory((Class<? extends ListGroup.ControllerFactory<T>>) annotation.controllerFactory());

        return ListOption.<T>createBuilder()
                .name(Component.translatable(this.getTranslationKey(field, null)))
                .description(this.description(field))
                .initial(valueFactory::provideNewValue)
                .controller(opt -> controllerFactory.createController(annotation, field, optionAccess, opt))
                .binding(new FieldBackedBinding<>(field.access(), field.defaultAccess()))
                .minimumNumberOfEntries(annotation.minEntries())
                .maximumNumberOfEntries(annotation.maxEntries() == 0 ? Integer.MAX_VALUE : annotation.maxEntries())
                .insertEntriesAtEnd(annotation.addEntriesToBottom())
                .build();
    }

    private OptionDescription description(ConfigField<List<T>> field) {
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

        return builder.build();
    }

    private ListGroup.ValueFactory<T> createValueFactory(Class<? extends ListGroup.ValueFactory<T>> clazz) {
        Constructor<? extends ListGroup.ValueFactory<T>> constructor;
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new YACLAutoGenException("Could not find no-args constructor for `valueFactory` on '%s' for @ListGroup field.".formatted(clazz.getName()), e);
        }

        try {
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new YACLAutoGenException("Couldn't invoke no-args constructor for `valueFactory` on '%s' for @ListGroup field.".formatted(clazz.getName()), e);
        }
    }

    private ListGroup.ControllerFactory<T> createControllerFactory(Class<? extends ListGroup.ControllerFactory<T>> clazz) {
        Constructor<? extends ListGroup.ControllerFactory<T>> constructor;
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new YACLAutoGenException("Could not find no-args constructor on `controllerFactory`, '%s' for @ListGroup field.".formatted(clazz.getName()), e);
        }

        try {
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new YACLAutoGenException("Couldn't invoke no-args constructor on `controllerFactory`, '%s' for @ListGroup field.".formatted(clazz.getName()), e);
        }
    }

    private String getTranslationKey(ConfigField<List<T>> field, @Nullable String suffix) {
        String key = "yacl3.config.%s.%s".formatted(field.parent().id().toString(), field.access().name());
        if (suffix != null) key += "." + suffix;
        return key;
    }
}
