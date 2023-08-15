package dev.isxander.yacl3.config.v2.api.autogen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An option factory that creates an instance
 * of a {@link dev.isxander.yacl3.api.LabelOption}.
 * <p>
 * The backing field can be private and final and
 * must be of type {@link net.minecraft.network.chat.Component}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Label {
}
