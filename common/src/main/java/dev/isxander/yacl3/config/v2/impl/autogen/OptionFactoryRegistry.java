package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.impl.utils.YACLConstants;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OptionFactoryRegistry {
    private static final Map<Class<?>, OptionFactory<?, ?>> factoryMap = new HashMap<>();

    static {
        registerOptionFactory(TickBox.class, new TickBoxImpl());
        registerOptionFactory(Boolean.class, new BooleanImpl());
        registerOptionFactory(IntSlider.class, new IntSliderImpl());
        registerOptionFactory(LongSlider.class, new LongSliderImpl());
        registerOptionFactory(FloatSlider.class, new FloatSliderImpl());
        registerOptionFactory(DoubleSlider.class, new DoubleSliderImpl());
        registerOptionFactory(IntField.class, new IntFieldImpl());
        registerOptionFactory(LongField.class, new LongFieldImpl());
        registerOptionFactory(FloatField.class, new FloatFieldImpl());
        registerOptionFactory(DoubleField.class, new DoubleFieldImpl());
        registerOptionFactory(EnumCycler.class, new EnumCyclerImpl());
        registerOptionFactory(StringField.class, new StringFieldImpl());
        registerOptionFactory(ColorRGBA.class, new ColorRGBAImpl());
        registerOptionFactory(Label.class, new LabelImpl());
        registerOptionFactory(ListGroup.class, new ListGroupImpl<>());

        registerOptionFactory(MasterTickBox.class, new MasterTickBoxImpl());
    }

    public static <A extends Annotation, T> void registerOptionFactory(Class<A> annotation, OptionFactory<A, T> factory) {
        factoryMap.put(annotation, factory);
    }

    public static <T> Optional<Option<T>> createOption(Field field, ConfigField<T> configField, OptionStorage storage) {
        Annotation[] annotations = Arrays.stream(field.getAnnotations())
                .filter(annotation -> factoryMap.containsKey(annotation.annotationType()))
                .toArray(Annotation[]::new);

        if (annotations.length != 1) {
            YACLConstants.LOGGER.warn("Found {} option factory annotations on field {}, expected 1", annotations.length, field);

            if (annotations.length == 0) {
                return Optional.empty();
            }
        }

        Annotation annotation = annotations[0];
        // noinspection unchecked
        OptionFactory<Annotation, T> factory = (OptionFactory<Annotation, T>) factoryMap.get(annotation.annotationType());
        return Optional.of(factory.createOption(annotation, configField, storage));
    }
}
