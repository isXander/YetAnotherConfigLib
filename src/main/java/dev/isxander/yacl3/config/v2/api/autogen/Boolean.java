package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An option factory.
 * <p>
 * This creates a regular option with a
 * {@link dev.isxander.yacl3.api.controller.BooleanControllerBuilder} controller.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Boolean {
    enum Formatter {
        YES_NO,
        TRUE_FALSE,
        ON_OFF,
        /**
         * Uses the translation keys:
         * <ul>
         *     <li>true: {@code yacl3.config.$configId.$fieldName.fmt.true}</li>
         *     <li>false: {@code yacl3.config.$configId.$fieldName.fmt.false}</li>
         * </ul>
         */
        CUSTOM,
    }

    /**
     * The format used to display the boolean.
     */
    Formatter formatter() default Formatter.TRUE_FALSE;

    /**
     * Whether to color the formatted text green and red
     * depending on the value: true or false respectively.
     */
    boolean colored() default false;
}
