package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.impl.autogen.OptionFactoryRegistry;

import java.lang.annotation.Annotation;

/**
 * The backing builder for option factories' annotations.
 * <p>
 * If you want to make a basic option with a controller, it's recommended
 * to use {@link SimpleOptionFactory} instead which is a subclass of this.
 *
 * @param <A> the annotation type
 * @param <T> the option's binding type
 */
public interface OptionFactory<A extends Annotation, T> {
    /**
     * Creates an option from the given annotation, backing field, and storage.
     *
     * @param annotation the annotation that fields are annotated with to use this factory
     * @param field the backing field
     * @param optionAccess the option access to access other options in the GUI
     * @return the built option to be added to the group/category
     */
    Option<T> createOption(A annotation, ConfigField<T> field, OptionAccess optionAccess);

    /**
     * Registers an option factory to be used by configs.
     *
     * @param annotationClass the class of the annotation to use a factory
     * @param factory an instance of the factory
     * @param <A> the type of the annotation
     * @param <T> the type of the option's binding
     */
    static <A extends Annotation, T> void register(Class<A> annotationClass, OptionFactory<A, T> factory) {
        OptionFactoryRegistry.registerOptionFactory(annotationClass, factory);
    }
}
