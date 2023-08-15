package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A regular option factory.
 * <p>
 * This creates a regular option with a
 * {@link dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder} controller.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DoubleField {
    /**
     * The minimum value of the field. If a user enters a value less
     * than this, it will be clamped to this value.
     * <p>
     * If this is set to {@code -Double.MAX_VALUE}, there will be no minimum.
     * <p>
     * If the current value is at this minimum, if available,
     * the translation key {@code yacl3.config.$configId.$fieldName.fmt.min}
     * will be used.
     */
    double min() default -Double.MAX_VALUE;

    /**
     * The maximum value of the field. If a user enters a value more
     * than this, it will be clamped to this value.
     * <p>
     * If this is set to {@code Double.MAX_VALUE}, there will be no minimum.
     * <p>
     * If the current value is at this maximum, if available,
     * the translation key {@code yacl3.config.$configId.$fieldName.fmt.max}
     * will be used.
     */
    double max() default Double.MAX_VALUE;

    /**
     * The format used to display the double.
     * This is the syntax used in {@link String#format(String, Object...)}.
     */
    String format() default "%.2f";
}
