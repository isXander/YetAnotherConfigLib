package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ListGroup {
    Class<? extends ValueFactory<?>> valueFactory();

    Class<? extends ControllerFactory<?>> controllerFactory();

    int maxEntries() default 0;
    int minEntries() default 0;

    interface ValueFactory<T> {
        T provideNewValue();
    }

    interface ControllerFactory<T> {
        ControllerBuilder<T> createController(ListGroup annotation, ConfigField<List<T>> field, OptionStorage storage, Option<T> option);
    }
}
