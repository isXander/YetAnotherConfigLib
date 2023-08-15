package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionFactoryRegistry;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionAccessImpl;
import dev.isxander.yacl3.config.v2.impl.autogen.YACLAutoGenException;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ConfigClassHandlerImpl<T> implements ConfigClassHandler<T> {
    private final Class<T> configClass;
    private final ResourceLocation id;
    private final boolean supportsAutoGen;
    private final ConfigSerializer<T> serializer;
    private final ConfigField<?>[] fields;

    private final T instance, defaults;

    public ConfigClassHandlerImpl(Class<T> configClass, ResourceLocation id, Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory) {
        this.configClass = configClass;
        this.id = id;
        this.supportsAutoGen = YACLPlatform.getEnvironment().isClient();

        try {
            Constructor<T> constructor = configClass.getDeclaredConstructor();
            this.instance = constructor.newInstance();
            this.defaults = constructor.newInstance();
        } catch (Exception e) {
            throw new YACLAutoGenException("Failed to create instance of config class '%s' with no-args constructor.".formatted(configClass.getName()), e);
        }

        this.fields = Arrays.stream(configClass.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> field.isAnnotationPresent(SerialEntry.class) || field.isAnnotationPresent(AutoGen.class))
                .map(field -> new ConfigFieldImpl<>(new ReflectionFieldAccess<>(field, instance), new ReflectionFieldAccess<>(field, defaults), this, field.getAnnotation(SerialEntry.class), field.getAnnotation(AutoGen.class)))
                .toArray(ConfigField[]::new);
        this.serializer = serializerFactory.apply(this);
    }

    @Override
    public T instance() {
        return this.instance;
    }

    @Override
    public T defaults() {
        return this.defaults;
    }

    @Override
    public Class<T> configClass() {
        return this.configClass;
    }

    @Override
    public ConfigField<?>[] fields() {
        return this.fields;
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public boolean supportsAutoGen() {
        return this.supportsAutoGen;
    }

    @Override
    public YetAnotherConfigLib generateGui() {
        if (!supportsAutoGen()) {
            throw new YACLAutoGenException("Auto GUI generation is not supported for this config class. You either need to enable it in the builder or you are attempting to create a GUI in a dedicated server environment.");
        }

        OptionAccessImpl storage = new OptionAccessImpl();
        Map<String, CategoryAndGroups> categories = new LinkedHashMap<>();
        for (ConfigField<?> configField : fields()) {
            configField.autoGen().ifPresent(autoGen -> {
                CategoryAndGroups groups = categories.computeIfAbsent(
                        autoGen.category(),
                        k -> new CategoryAndGroups(
                                ConfigCategory.createBuilder()
                                        .name(Component.translatable("yacl3.config.%s.category.%s".formatted(id().toString(), k))),
                                new LinkedHashMap<>()
                        )
                );
                OptionAddable group = groups.groups().computeIfAbsent(autoGen.group().orElse(""), k -> {
                    if (k.isEmpty())
                        return groups.category();
                    return OptionGroup.createBuilder()
                            .name(Component.translatable("yacl3.config.%s.category.%s.group.%s".formatted(id().toString(), autoGen.category(), k)));
                });

                Option<?> option;
                try {
                    option = createOption(configField, storage);
                } catch (Exception e) {
                    throw new YACLAutoGenException("Failed to create option for field '%s'".formatted(configField.access().name()), e);
                }

                storage.putOption(configField.access().name(), option);
                group.option(option);
            });
        }
        storage.checkBadOperations();
        categories.values().forEach(CategoryAndGroups::finaliseGroups);

        YetAnotherConfigLib.Builder yaclBuilder = YetAnotherConfigLib.createBuilder()
                .save(this.serializer()::save)
                .title(Component.translatable("yacl3.config.%s.title".formatted(this.id().toString())));
        categories.values().forEach(category -> yaclBuilder.category(category.category().build()));

        return yaclBuilder.build();
    }

    private <U> Option<U> createOption(ConfigField<U> configField, OptionAccess storage) {
        return OptionFactoryRegistry.createOption(((ReflectionFieldAccess<?>) configField.access()).field(), configField, storage)
                .orElseThrow(() -> new YACLAutoGenException("Failed to create option for field %s".formatted(configField.access().name())));
    }

    @Override
    public ConfigSerializer<T> serializer() {
        return this.serializer;
    }

    public static class BuilderImpl<T> implements Builder<T> {
        private final Class<T> configClass;
        private ResourceLocation id;
        private Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory;

        public BuilderImpl(Class<T> configClass) {
            this.configClass = configClass;
        }

        @Override
        public Builder<T> id(ResourceLocation id) {
            this.id = id;
            return this;
        }

        @Override
        public Builder<T> serializer(Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory) {
            this.serializerFactory = serializerFactory;
            return this;
        }

        @Override
        public ConfigClassHandler<T> build() {
            return new ConfigClassHandlerImpl<>(configClass, id, serializerFactory);
        }
    }

    private record CategoryAndGroups(ConfigCategory.Builder category, Map<String, OptionAddable> groups) {
        private void finaliseGroups() {
            groups.forEach((name, group) -> {
                if (group instanceof OptionGroup.Builder groupBuilder) {
                    category.group(groupBuilder.build());
                }
            });
        }
    }
}
