package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A regular option factory.
 * <p>
 * This creates a regular option with a
 * {@link dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder} controller.
 * <p>
 * If available, the translation key {@code yacl3.config.$configId.$fieldName.fmt.$value}
 * is used where {@code $value} is the current value of the option, for example, {@code 5}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IntSlider {
    /**
     * The minimum value of the slider.
     */
    int min();

    /**
     * The maximum value of the slider.
     */
    int max();

    /**
     * The step size of this slider.
     * <p>
     * For example, if this is set to 1, the slider will
     * increment/decrement by 1 when dragging, no less, no more and
     * will always be a multiple of 1.
     */
    int step();

    /**
     * The format used to display the integer.
     * This is the syntax used in {@link String#format(String, Object...)}.
     */
    String format() default "%d";
}
