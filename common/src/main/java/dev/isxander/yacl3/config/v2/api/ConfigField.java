package dev.isxander.yacl3.config.v2.api;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ConfigField<T> {
    String serialName();

    Optional<String> comment();

    FieldAccess<T> access();

    @Nullable OptionFactory<T> factory();

    boolean supportsFactory();
}
