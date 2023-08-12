package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.config.v2.api.ConfigEntry;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.FieldAccess;
import dev.isxander.yacl3.config.v2.api.OptionFactory;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class ConfigFieldImpl<T> implements ConfigField<T> {
    private final @Nullable OptionFactory<T> factory;
    private final String serialName;
    private final Optional<String> comment;
    private final FieldAccess<T> field;
    private final boolean autoGen;

    public ConfigFieldImpl(boolean auto, ConfigEntry entry, FieldAccess<T> field) {
        this.serialName = "".equals(entry.serialName()) ? field.name() : entry.serialName();
        this.comment = "".equals(entry.comment()) ? Optional.empty() : Optional.of(entry.comment());
        this.factory = auto ? makeFactory(entry.factory(), this.serialName) : null;
        this.autoGen = auto;
        this.field = field;
    }

    @Override
    public String serialName() {
        return this.serialName;
    }

    @Override
    public Optional<String> comment() {
        return this.comment;
    }

    @Override
    public FieldAccess<T> access() {
        return field;
    }

    @Override
    public @Nullable OptionFactory<T> factory() {
        return factory;
    }

    @Override
    public boolean supportsFactory() {
        return this.autoGen;
    }

    private OptionFactory<T> makeFactory(Class<? extends OptionFactory<?>> clazz, String name) {
        if (clazz.equals(DefaultOptionFactory.class)) {
            throw new NotImplementedException("Field '%s' does not have an option factory, but auto-gen is enabled.".formatted(this.serialName()));
        }

        Constructor<?> constructor;

        try {
            constructor = clazz.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Failed to find (String) constructor for option factory %s.".formatted(clazz.getName()), e);
        }

        try {
            return (OptionFactory<T>) constructor.newInstance(name);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Failed to cast option factory %s to OptionFactory<%s>.".formatted(clazz.getName(), field.type().getTypeName()), e);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create new option factory (class is '%s')".formatted(clazz.getName()), e);
        }
    }
}
