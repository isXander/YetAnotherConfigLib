package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An option factory like {@link TickBox} but controls
 * other options' availability based on its state.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MasterTickBox {
    /**
     * The exact names of the fields with {@link AutoGen} annotation
     * to control the availability of.
     */
    String[] value();

    /**
     * Whether having the tickbox disabled should enable the options
     * rather than disable.
     */
    boolean invert() default false;
}
