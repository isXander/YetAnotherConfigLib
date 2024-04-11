package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows you to specify a custom value formatter
 * in the form of a translation key.
 * <p>
 * Without this annotation, the value will be formatted
 * according to the option factory, implementation details
 * for that should be found in the javadoc for the factory.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FormatTranslation {
    /**
     * The translation key for the value formatter.
     * One parameter is passed to this key: the option's value,
     * using {@link Object#toString()}.
     */
    String value() default "";
}
