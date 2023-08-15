package dev.isxander.yacl3.config.v2.api;

import java.lang.reflect.Type;

/**
 * An abstract interface for accessing properties of an instance of a field.
 * You do not need to worry about exceptions as the implementation
 * will handle them.
 *
 * @param <T> the type of the field
 */
public interface ReadOnlyFieldAccess<T> {
    /**
     * @return the current value of the field.
     */
    T get();

    /**
     * @return the name of the field.
     */
    String name();

    /**
     * @return the type of the field.
     */
    Type type();

    /**
     * @return the class of the field.
     */
    Class<T> typeClass();
}
