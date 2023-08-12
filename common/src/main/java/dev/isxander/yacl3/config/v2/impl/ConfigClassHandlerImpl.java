package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.platform.YACLPlatform;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.function.Function;

public class ConfigClassHandlerImpl<T> implements ConfigClassHandler<T> {
    private final Class<T> configClass;
    private final boolean supportsAutoGen;
    private final ConfigSerializer<T> serializer;
    private final ConfigField<?>[] fields;

    private final T instance, defaults;

    public ConfigClassHandlerImpl(Class<T> configClass, Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory, boolean autoGen) {
        this.configClass = configClass;
        this.supportsAutoGen = YACLPlatform.getEnvironment().isClient() && autoGen;

        try {
            Constructor<T> constructor = configClass.getDeclaredConstructor();
            this.instance = constructor.newInstance();
            this.defaults = constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create instance of config class '%s' with no-args constructor.".formatted(configClass.getName()), e);
        }

        this.fields = Arrays.stream(configClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ConfigEntry.class))
                .map(field -> new ConfigFieldImpl<>(this.supportsAutoGen(), field.getAnnotation(ConfigEntry.class), new ReflectionFieldAccess<>(field, instance)))
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
    public boolean supportsAutoGen() {
        return this.supportsAutoGen;
    }

    @Override
    public YetAnotherConfigLib generateGui() {
        Validate.isTrue(supportsAutoGen(), "Auto GUI generation is not supported for this config class. You either need to enable it in the builder or you are attempting to create a GUI in a dedicated server environment.");

        throw new IllegalStateException();
    }

    @Override
    public ConfigSerializer<T> serializer() {
        return this.serializer;
    }

    public static class BuilderImpl<T> implements Builder<T> {
        private final Class<T> configClass;
        private Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory;
        private boolean autoGen;

        public BuilderImpl(Class<T> configClass) {
            this.configClass = configClass;
        }

        @Override
        public Builder<T> serializer(Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory) {
            this.serializerFactory = serializerFactory;
            return this;
        }

        @Override
        public Builder<T> autoGen(boolean autoGen) {
            throw new IllegalArgumentException();
        }

        @Override
        public ConfigClassHandler<T> build() {
            return new ConfigClassHandlerImpl<>(configClass, serializerFactory, autoGen);
        }
    }
}
