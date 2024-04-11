package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A regular option factory.
 * <p>
 * This creates a regular option with a
 * {@link dev.isxander.yacl3.api.controller.LongFieldControllerBuilder} controller.
 * <p>
 * If available, the translation key {@code yacl3.config.$configId.$fieldName.fmt.$value}
 * is used where {@code $value} is the current value of the option, for example, {@code 5}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LongField {
    /**
     * The minimum value of the field. If a user enters a value less
     * than this, it will be clamped to this value.
     * <p>
     * If this is set to {@code Long.MIN_VALUE}, there will be no minimum.
     */
    long min() default Long.MIN_VALUE;

    /**
     * The maximum value of the field. If a user enters a value more
     * than this, it will be clamped to this value.
     * <p>
     * If this is set to {@code Long.MAX_VALUE}, there will be no minimum.
     */
    long max() default Long.MAX_VALUE;

    /**
     * The format used to display the long.
     * This is the syntax used in {@link String#format(String, Object...)}.
     */
    String format() default "%.0f";
}
