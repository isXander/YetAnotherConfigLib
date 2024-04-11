package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A regular option factory.
 * <p>
 * This creates a regular option with a
 * {@link dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder} controller.
 * <p>
 * If available, the translation key {@code yacl3.config.$configId.$fieldName.fmt.$value}
 * is used where {@code $value} is the current value of the option, for example, {@code 5}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IntField {
    /**
     * The minimum value of the field. If a user enters a value less
     * than this, it will be clamped to this value.
     * <p>
     * If this is set to {@code Integer.MIN_VALUE}, there will be no minimum.
     */
    int min() default Integer.MIN_VALUE;

    /**
     * The minimum value of the field. If a user enters a value more
     * than this, it will be clamped to this value.
     * <p>
     * If this is set to {@code Integer.MAX_VALUE}, there will be no minimum.
     */
    int max() default Integer.MAX_VALUE;

    /**
     * The format used to display the integer.
     * This is the syntax used in {@link String#format(String, Object...)}.
     */
    String format() default "%.0f";
}
