package dev.isxander.yacl3.config.v2.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as serializable, so it can be used in a {@link ConfigSerializer}.
 * Any field without this annotation will not be saved or loaded, but can still be turned
 * into an auto-generated option.
 * <p>
 * You can also annotate a class with this to mark all fields as serializable.
 * You can override the default behaviour by annotating a field with {@link SerialEntry}.
 * Only the `required` and `nullable` parameters are permitted on classes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
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

    /**
     * Whether the field is required in the loaded config to be valid.
     * If it's not, the config will be marked as dirty and re-saved with the default value.
     */
    boolean required() default true;

    /**
     * Whether the field can be null.
     */
    boolean nullable() default false;
}
