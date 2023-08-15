package dev.isxander.yacl3.config.v2.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as serializable, so it can be used in a {@link ConfigSerializer}.
 * Any field without this annotation will not be saved or loaded, but can still be turned
 * into an auto-generated option.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SerialEntry {
    /**
     * The serial name of the field.
     * If empty, the serializer will decide the name.
     */
    String value() default "";

    /**
     * The comment to add to the field.
     * Some serializers may not support this.
     * If empty, the serializer will not add a comment.
     */
    String comment() default "";
}
