package dev.isxander.yacl3.config.v2.api;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.impl.ConfigClassHandlerImpl;

import java.util.function.Function;

public interface ConfigClassHandler<T> {
    T instance();

    T defaults();

    Class<T> configClass();

    ConfigField<?>[] fields();

    YetAnotherConfigLib generateGui();

    boolean supportsAutoGen();

    ConfigSerializer<T> serializer();

    static <T> Builder<T> createBuilder(Class<T> configClass) {
        return new ConfigClassHandlerImpl.BuilderImpl<>(configClass);
    }

    interface Builder<T> {
        Builder<T> serializer(Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory);

        Builder<T> autoGen(boolean autoGen);

        ConfigClassHandler<T> build();
    }
}
