package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionFactoryRegistry;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionAccessImpl;
import dev.isxander.yacl3.config.v2.impl.autogen.YACLAutoGenException;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigClassHandlerImpl<T> implements ConfigClassHandler<T> {
    private final Class<T> configClass;
    private final ResourceLocation id;
    private final boolean supportsAutoGen;
    private final ConfigSerializer<T> serializer;
    private final ConfigFieldImpl<?>[] fields;

    private T instance;
    private final T defaults;
    private final Constructor<T> noArgsConstructor;

    public ConfigClassHandlerImpl(Class<T> configClass, ResourceLocation id, Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory) {
        this.configClass = configClass;
        this.id = id;
        this.supportsAutoGen = id != null && YACLPlatform.getEnvironment().isClient();

        try {
            noArgsConstructor = configClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new YACLAutoGenException("Failed to find no-args constructor for config class %s.".formatted(configClass.getName()), e);
        }
        this.instance = createNewObject();
        this.defaults = createNewObject();

        this.fields = Arrays.stream(configClass.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> field.isAnnotationPresent(SerialEntry.class) || field.isAnnotationPresent(AutoGen.class))
                .map(field -> new ConfigFieldImpl<>(new ReflectionFieldAccess<>(field, instance), new ReflectionFieldAccess<>(field, defaults), this, field.getAnnotation(SerialEntry.class), field.getAnnotation(AutoGen.class)))
                .toArray(ConfigFieldImpl[]::new);
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
    public ConfigFieldImpl<?>[] fields() {
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

        boolean hasAutoGenFields = Arrays.stream(fields()).anyMatch(field -> field.autoGen().isPresent());

        if (!hasAutoGenFields) {
            throw new YACLAutoGenException("No fields in this config class are annotated with @AutoGen. You must annotate at least one field with @AutoGen to generate a GUI.");
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

    @Override
    public boolean load() {
        // create a new instance to load into
        T newInstance = createNewObject();

        // create field accesses for the new object
        Map<ConfigFieldImpl<?>, ReflectionFieldAccess<?>> accessBufferImpl = Arrays.stream(fields())
                .map(field -> new AbstractMap.SimpleImmutableEntry<>(
                        field,
                        new ReflectionFieldAccess<>(field.access().field(), newInstance)
                ))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // convert the map into API safe field accesses
        Map<ConfigField<?>, FieldAccess<?>> accessBuffer = accessBufferImpl.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // attempt to load the config
        ConfigSerializer.LoadResult loadResult = ConfigSerializer.LoadResult.FAILURE;
        Throwable error = null;
        try {
            loadResult = this.serializer().loadSafely(accessBuffer);
        } catch (Throwable e) {
            // handle any errors later in the loadResult switch case
            error = e;
        }

        switch (loadResult) {
            case DIRTY:
            case SUCCESS:
                // replace the instance with the newly created one
                this.instance = newInstance;
                for (ConfigFieldImpl<?> field : fields()) {
                    // update the field accesses to point to the correct object
                    ((ConfigFieldImpl<Object>) field).setFieldAccess((ReflectionFieldAccess<Object>) accessBufferImpl.get(field));
                }

                if (loadResult == ConfigSerializer.LoadResult.DIRTY) {
                    // if the load result is dirty, we need to save the config again
                    this.save();
                }
            case NO_CHANGE:
                return true;
            case FAILURE:
                YACLConstants.LOGGER.error(
                        "Unsuccessful load of config class '{}'. The load will be abandoned and config remains unchanged.",
                        configClass.getSimpleName(), error
                );
        }

        return false;
    }

    @Override
    public void save() {
        serializer().save();
    }

    private T createNewObject() {
        try {
            return noArgsConstructor.newInstance();
        } catch (Exception e) {
            throw new YACLAutoGenException("Failed to create instance of config class '%s' with no-args constructor.".formatted(configClass.getName()), e);
        }
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
            Validate.notNull(serializerFactory, "serializerFactory must not be null");
            Validate.notNull(configClass, "configClass must not be null");

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
