package dev.isxander.yacl3.config.v2.api;

import dev.isxander.yacl3.config.v2.impl.DefaultOptionFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigEntry {
    Class<? extends OptionFactory<?>> factory() default DefaultOptionFactory.class;

    String serialName() default "";

    String comment() default "";
}
