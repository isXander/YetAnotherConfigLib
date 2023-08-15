package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * An option factory.
 * <p>
 * This creates a List option with a custom controller.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ListGroup {
    /**
     * The {@link Class} representing a class that implements {@link ValueFactory}.
     * To create a new instance for the list when the user adds a new entry to the list.
     * Remember this class can be shared with {@link ControllerFactory} as well.
     */
    Class<? extends ValueFactory<?>> valueFactory();

    /**
     * The {@link Class} representing a class that implements {@link ControllerBuilder}
     * to add a controller to every entry in the list.
     * Remember this class can be shared with {@link ValueFactory} as well.
     */
    Class<? extends ControllerFactory<?>> controllerFactory();

    /**
     * The maximum number of entries that can be added to the list.
     * Once at this limit, the add button is disabled.
     * If this is equal to {@code 0}, there is no limit.
     */
    int maxEntries() default 0;

    /**
     * The minimum number of entries that must be in the list.
     * When at this limit, the remove button of the entries is disabled.
     */
    int minEntries() default 0;

    /**
     * Whether to add new entries at the bottom of the list rather than the top.
     */
    boolean addEntriesToBottom() default false;

    interface ValueFactory<T> {
        T provideNewValue();
    }

    interface ControllerFactory<T> {
        ControllerBuilder<T> createController(ListGroup annotation, ConfigField<List<T>> field, OptionAccess storage, Option<T> option);
    }
}
