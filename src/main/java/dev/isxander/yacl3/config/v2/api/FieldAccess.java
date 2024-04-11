package dev.isxander.yacl3.config.v2.api;

/**
 * A writable field instance access.
 *
 * @param <T> the type of the field
 */
public interface FieldAccess<T> extends ReadOnlyFieldAccess<T> {
    /**
     * Sets the value of the field.
     * @param value the value to set
     */
    void set(T value);
}
