package dev.isxander.yacl3.config.v2.api.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.ConfigEntry;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.impl.serializer.GsonConfigSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.awt.*;
import java.nio.file.Path;
import java.util.function.UnaryOperator;

/**
 * Uses GSON to serialize and deserialize config data from JSON to a file.
 * <p>
 * Only fields annotated with {@link dev.isxander.yacl3.config.v2.api.SerialEntry} are included in the JSON.
 * {@link Component}, {@link Style} and {@link Color} have default type adapters, so there is no need to provide them in your GSON instance.
 * GSON is automatically configured to format fields as {@code lower_camel_case}.
 * <p>
 * Optionally, this can also be written under JSON5 spec, allowing comments.
 *
 * @param <T> config data type
 */
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
     * For example, if you wanted to revert YACL's lower_camel_case naming policy,
     * you could do the following:
     * <pre>
     * {@code
     * GsonConfigSerializerBuilder.create(config)
     *         .appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY))
     * }
     * </pre>
     *
     * @param gsonBuilder the function to apply to the builder
     */
    GsonConfigSerializerBuilder<T> appendGsonBuilder(UnaryOperator<GsonBuilder> gsonBuilder);

    /**
     * Writes the json under JSON5 spec, allowing the use of {@link SerialEntry#comment()}.
     * If enabling this option it's recommended to use the file extension {@code .json5}.
     *
     * @param json5 whether to write under JSON5 spec
     * @return this builder
     */
    GsonConfigSerializerBuilder<T> setJson5(boolean json5);

    ConfigSerializer<T> build();
}
