package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.ValueFormattableController;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.config.v2.api.ReadOnlyFieldAccess;
import dev.isxander.yacl3.config.v2.api.autogen.OverrideFormatter;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class AutoGenUtils {
    public static <T> void addCustomFormatterToController(ControllerBuilder<T> controller, Optional<OverrideFormatter> formatter, ReadOnlyFieldAccess<T> field) {
        formatter.ifPresent(formatterClass -> {
            if (controller instanceof ValueFormattableController<?,?>) {
                ValueFormattableController<T, ?> typedBuilder;
                try {
                    typedBuilder = (ValueFormattableController<T, ?>) controller;
                } catch (ClassCastException e) {
                    throw new YACLAutoGenException("'%s': The formatter class on @CustomFormatter is of incorrect type. Expected %s, got %s.".formatted(field.name(), field.type().getTypeName(), formatterClass.value().getTypeParameters()[0].getName()));
                }

                try {
                    typedBuilder.formatValue((ValueFormatter<T>) formatterClass.value().getConstructor().newInstance());
                } catch (Exception e) {
                    throw new YACLAutoGenException("'%s': Failed to instantiate formatter class %s.".formatted(field.name(), formatterClass.value().getName()), e);
                }
            } else {
                throw new YACLAutoGenException("Attempted to use @CustomFormatter on an option factory for field '%s' that uses a controller that does not support this.".formatted(field.name()));
            }
        });
    }

    public static <T> T constructNoArgsClass(Class<T> clazz, Supplier<String> constructorNotFoundConsumer, Supplier<String> constructorFailedConsumer) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            throw new YACLAutoGenException(constructorNotFoundConsumer.get(), e);
        } catch (Exception e) {
            throw new YACLAutoGenException(constructorFailedConsumer.get(), e);
        }
    }
}
