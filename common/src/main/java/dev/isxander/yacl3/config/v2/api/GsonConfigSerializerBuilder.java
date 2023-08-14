package dev.isxander.yacl3.config.v2.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.v2.impl.serializer.GsonConfigSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.awt.*;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

public interface GsonConfigSerializerBuilder<T> {
    static <T> GsonConfigSerializerBuilder<T> create(ConfigClassHandler<T> config) {
        return new GsonConfigSerializer.Builder<>(config);
    }

    /**
     * Sets the file path to save and load the config from.
     */
    GsonConfigSerializerBuilder<T> setPath(Path path);

    /**
     * Sets the GSON instance to use. Overrides all YACL defaults such as:
     * <ul>
     *     <li>lower_camel_case field naming policy</li>
     *     <li>null serialization</li>
     *     <li>{@link Component}, {@link Style} and {@link Color} type adapters</li>
     * </ul>
     * Still respects the exclusion strategy to only serialize {@link ConfigEntry}
     * but these can be added to with setExclusionStrategies.
     *
     * @param gsonBuilder gson builder to use
     */
    GsonConfigSerializerBuilder<T> overrideGsonBuilder(GsonBuilder gsonBuilder);

    /**
     * Sets the GSON instance to use. Overrides all YACL defaults such as:
     * <ul>
     *     <li>lower_camel_case field naming policy</li>
     *     <li>null serialization</li>
     *     <li>{@link Component}, {@link Style} and {@link Color} type adapters</li>
     * </ul>
     * Still respects the exclusion strategy to only serialize {@link ConfigEntry}
     * but these can be added to with setExclusionStrategies.
     *
     * @param gson gson instance to be converted to a builder
     */
    GsonConfigSerializerBuilder<T> overrideGsonBuilder(Gson gson);

    /**
     * Appends extra configuration to a GSON builder.
     * This is the intended way to add functionality to the GSON instance.
     * <p>
     * By default, YACL sets the GSON with the following options:
     * <ul>
     *     <li>lower_camel_case field naming policy</li>
     *     <li>null serialization</li>
     *     <li>{@link Component}, {@link Style} and {@link Color} type adapters</li>
     * </ul>
     *
     * @param gsonBuilder the function to apply to the builder
     */
    GsonConfigSerializerBuilder<T> appendGsonBuilder(UnaryOperator<GsonBuilder> gsonBuilder);

    GsonConfigSerializerBuilder<T> setJson5(boolean json5);

    ConfigSerializer<T> build();
}
