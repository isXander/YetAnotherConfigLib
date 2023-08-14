package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGenField;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConfigFieldImpl<T> implements ConfigField<T> {
    private final FieldAccess<T> field;
    private final ReadOnlyFieldAccess<T> defaultField;
    private final ConfigClassHandler<?> parent;
    private final Optional<SerialField> serial;
    private final Optional<AutoGenField<T>> autoGen;

    public ConfigFieldImpl(FieldAccess<T> field, ReadOnlyFieldAccess<T> defaultField, ConfigClassHandler<?> parent, @Nullable SerialEntry config, @Nullable AutoGen autoGen) {
        this.field = field;
        this.defaultField = defaultField;
        this.parent = parent;

        this.serial = config != null
                ? Optional.of(
                new SerialFieldImpl(
                        "".equals(config.value()) ? field.name() : config.value(),
                        "".equals(config.comment()) ? Optional.empty() : Optional.of(config.comment())
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
    public FieldAccess<T> access() {
        return field;
    }

    @Override
    public ReadOnlyFieldAccess<T> defaultAccess() {
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
    public Optional<AutoGenField<T>> autoGen() {
        return this.autoGen;
    }

    private record SerialFieldImpl(String serialName, Optional<String> comment) implements SerialField {
    }
    private record AutoGenFieldImpl<T>(String category, Optional<String> group) implements AutoGenField<T> {
    }
}
