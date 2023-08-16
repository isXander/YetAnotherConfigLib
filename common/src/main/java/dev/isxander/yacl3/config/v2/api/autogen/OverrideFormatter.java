package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.controller.ValueFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows you to specify a custom {@link ValueFormatter} for a field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OverrideFormatter {
    Class<? extends ValueFormatter<?>> value();
}
