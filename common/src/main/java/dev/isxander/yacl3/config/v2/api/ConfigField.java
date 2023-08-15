package dev.isxander.yacl3.config.v2.api;

import dev.isxander.yacl3.config.v2.api.autogen.AutoGenField;

import java.util.Optional;

/**
 * Represents a field in a config class.
 * This is used to get all metadata on a field,
 * and access the field and its default value.
 *
 * @param <T> the field's type
 */
public interface ConfigField<T> {
    /**
     * Gets the accessor for the field on the main instance.
     * (Accessed through {@link ConfigClassHandler#instance()})
     */
    FieldAccess<T> access();

    /**
     * Gets the accessor for the field on the default instance.
     */
    ReadOnlyFieldAccess<T> defaultAccess();

    /**
     * @return the parent config class handler that manages this field.
     */
    ConfigClassHandler<?> parent();

    /**
     * The serial entry metadata for this field, if it exists.
     */
    Optional<SerialField> serial();

    /**
     * The auto-gen metadata for this field, if it exists.
     */
    Optional<AutoGenField> autoGen();
}
