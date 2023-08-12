package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGenField;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConfigFieldImpl<T> implements ConfigField<T> {
    private ReflectionFieldAccess<T> field;
    private final ReflectionFieldAccess<T> defaultField;
    private final ConfigClassHandler<?> parent;
    private final Optional<SerialField> serial;
    private final Optional<AutoGenField> autoGen;

    public ConfigFieldImpl(ReflectionFieldAccess<T> field, ReflectionFieldAccess<T> defaultField, ConfigClassHandler<?> parent, @Nullable SerialEntry config, @Nullable AutoGen autoGen) {
        this.field = field;
        this.defaultField = defaultField;
        this.parent = parent;

        this.serial = config != null
                ? Optional.of(
                new SerialFieldImpl(
                        "".equals(config.value()) ? field.name() : config.value(),
                        "".equals(config.comment()) ? Optional.empty() : Optional.of(config.comment()),
                        config.required(),
                        config.nullable()
                )
        )
                : Optional.empty();
        this.autoGen = autoGen != null
                ? Optional.of(
                new AutoGenFieldImpl<>(
                        autoGen.category(),
                        "".equals(autoGen.group()) ? Optional.empty() : Optional.of(autoGen.group())
                )
        )
                : Optional.empty();
    }

    @Override
    public ReflectionFieldAccess<T> access() {
        return field;
    }

    public void setFieldAccess(ReflectionFieldAccess<T> field) {
        this.field = field;
    }

    @Override
    public ReflectionFieldAccess<T> defaultAccess() {
        return defaultField;
    }

    @Override
    public ConfigClassHandler<?> parent() {
        return parent;
    }

    @Override
    public Optional<SerialField> serial() {
        return this.serial;
    }

    @Override
    public Optional<AutoGenField> autoGen() {
        return this.autoGen;
    }

    private record SerialFieldImpl(String serialName, Optional<String> comment, boolean required, boolean nullable) implements SerialField {
    }
    private record AutoGenFieldImpl<T>(String category, Optional<String> group) implements AutoGenField {
    }
}
