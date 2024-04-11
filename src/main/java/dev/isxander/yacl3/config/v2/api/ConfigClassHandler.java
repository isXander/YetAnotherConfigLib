package dev.isxander.yacl3.config.v2.api;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.impl.ConfigClassHandlerImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * Represents a handled config class.
 *
 * @param <T> the backing config class to be managed
 */
public interface ConfigClassHandler<T> {
    /**
     * Gets the working instance of the config class.
     * This should be used to get and set fields like usual.
     */
    T instance();

    /**
     * Gets a second instance of the config class that
     * should be used to get default values only. No fields
     * should be modified in this instance.
     */
    T defaults();

    /**
     * Gets the class of the config.
     */
    Class<T> configClass();

    /**
     * Get all eligible fields in the config class.
     * They could either be annotated with {@link dev.isxander.yacl3.config.v2.api.autogen.AutoGen}
     * or {@link SerialEntry}, do not assume that a field has both of these.
     */
    ConfigField<?>[] fields();

    /**
     * The unique identifier of this config handler.
     */
    ResourceLocation id();

    /**
     * Auto-generates a GUI for this config class.
     * This throws an exception if auto-gen is not supported.
     */
    YetAnotherConfigLib generateGui();

    /**
     * Whether this config class supports auto-gen.
     * If on a dedicated server, this returns false.
     */
    boolean supportsAutoGen();

    /**
     * Safely loads the config class using the provided serializer.
     * @return if the config was loaded successfully
     */
    boolean load();

    /**
     * Safely saves the config class using the provided serializer.
     */
    void save();

    /**
     * The serializer for this config class.
     * Manages saving and loading of the config with fields
     * annotated with {@link SerialEntry}.
     *
     * @deprecated use {@link #load()} and {@link #save()} instead.
     */
    @Deprecated
    ConfigSerializer<T> serializer();

    /**
     * Creates a builder for a config class.
     *
     * @param configClass the config class to build
     * @param <T> the type of the config class
     * @return the builder
     */
    static <T> Builder<T> createBuilder(Class<T> configClass) {
        return new ConfigClassHandlerImpl.BuilderImpl<>(configClass);
    }

    interface Builder<T> {
        /**
         * The unique identifier of this config handler.
         * The namespace should be your modid.
         *
         * @return this builder
         */
        Builder<T> id(ResourceLocation id);

        /**
         * The function to create the serializer for this config class.
         *
         * @return this builder
         */
        Builder<T> serializer(Function<ConfigClassHandler<T>, ConfigSerializer<T>> serializerFactory);

        ConfigClassHandler<T> build();
    }
}
