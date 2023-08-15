package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A regular option factory.
 * <p>
 * This creates a regular option with a
 * {@link dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder} controller.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FloatSlider {
    /**
     * The minimum value of the slider.
     * <p>
     * If the current value is at this minimum, if available,
     * the translation key {@code yacl3.config.$configId.$fieldName.fmt.min}
     * will be used.
     */
    float min();

    /**
     * The maximum value of the slider.
     * <p>
     * If the current value is at this maximum, if available,
     * the translation key {@code yacl3.config.$configId.$fieldName.fmt.max}
     * will be used.
     */
    float max();

    /**
     * The step size of this slider.
     * For example, if this is set to 0.1, the slider will
     * increment/decrement by 0.1 when dragging, no less, no more and
     * will always be a multiple of 0.1.
     */
    float step();

    /**
     * The format used to display the float.
     * This is the syntax used in {@link String#format(String, Object...)}.
     */
    String format() default "%.1f";
}
