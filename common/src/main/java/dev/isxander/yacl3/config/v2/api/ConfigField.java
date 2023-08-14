package dev.isxander.yacl3.config.v2.api;

import dev.isxander.yacl3.config.v2.api.autogen.AutoGenField;

import java.util.Optional;

public interface ConfigField<T> {
    FieldAccess<T> access();

    ReadOnlyFieldAccess<T> defaultAccess();

    ConfigClassHandler<?> parent();

    Optional<SerialField> serial();

    Optional<AutoGenField<T>> autoGen();
}
