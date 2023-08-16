package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the name of an auto-generated option.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OverrideName {
    /**
     * The translation key to use for the option's name.
     */
    String value() default "";
}
