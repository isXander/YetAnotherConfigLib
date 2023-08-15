package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Any field that is annotated with this will generate a config option
 * in the auto-generated config GUI. This should be paired with an
 * {@link OptionFactory} annotation to define how to create the option.
 * Some examples of this are {@link TickBox}, {@link FloatSlider}, {@link Label} or {@link StringField}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoGen {
    /**
     * Should be the id of the category. This is used to group options.
     * The translation keys also use this. Category IDs can be set as a
     * {@code private static final String} and used in the annotation to prevent
     * repeating yourself.
     */
    String category();

    /**
     * If left blank, the option will go in the root group, where it is
     * listed at the top of the category with no group header. If set,
     * this also appends to the translation key. Group IDs can be reused
     * between multiple categories.
     */
    String group() default "";
}
