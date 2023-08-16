package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.impl.autogen.EmptyCustomImageFactory;
import dev.isxander.yacl3.gui.ImageRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OverrideImage {
    String value() default "";

    int width() default 0;
    int height() default 0;

    Class<? extends CustomImageFactory<?>> factory() default EmptyCustomImageFactory.class;

    interface CustomImageFactory<T> {
        CompletableFuture<Optional<ImageRenderer>> createImage(T value, ConfigField<T> field, OptionAccess access);
    }
}
