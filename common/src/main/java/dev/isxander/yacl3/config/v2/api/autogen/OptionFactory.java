package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionFactoryRegistry;

import java.lang.annotation.Annotation;

public interface OptionFactory<A extends Annotation, T> {
    Option<T> createOption(A annotation, ConfigField<T> field, OptionStorage storage);

    static <A extends Annotation, T> void register(Class<A> annotationClass, OptionFactory<A, T> factory) {
        OptionFactoryRegistry.registerOptionFactory(annotationClass, factory);
    }
}
