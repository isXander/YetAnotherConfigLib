package dev.isxander.yacl3.config;

import com.google.gson.*;
import dev.isxander.yacl3.config.v2.impl.serializer.GsonConfigSerializer;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.UnaryOperator;

/**
 * Uses GSON to serialize and deserialize config data from JSON to a file.
 * <p>
 * Only fields annotated with {@link ConfigEntry} are included in the JSON.
 * {@link Component}, {@link Style} and {@link Color} have default type adapters, so there is no need to provide them in your GSON instance.
 * GSON is automatically configured to format fields as {@code lower_camel_case}.
 *
 * @param <T> config data type
 * @deprecated upgrade to config v2 {@link dev.isxander.yacl3.config.v2.api.ConfigClassHandler} with {@link dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder}
 *          <pre>
 * {@code
 * public class MyConfig {
 *     public static ConfigClassHandler<MyConfig> HANDLER = ConfigClassHandler.createBuilder(MyConfig.class)
 *             .id(new ResourceLocation("modid", "config"))
 *             .serializer(config -> GsonConfigSerializerBuilder.create(config)
 *                     .setPath(FabricLoader.getInstance().getConfigDir().resolve("my_mod.json")
 *                     .build())
 *             .build();
 *
 *     @SerialEntry public boolean myBoolean = true;
 * }
 * }
 * </pre>
 */
@Deprecated
public class GsonConfigInstance<T> extends ConfigInstance<T> {
    private final Gson gson;
    private final Path path;

    @Deprecated
    public GsonConfigInstance(Class<T> configClass, Path path) {
        this(configClass, path, new GsonBuilder());
    }

    @Deprecated
    public GsonConfigInstance(Class<T> configClass, Path path, Gson gson) {
        this(configClass, path, gson.newBuilder());
    }

    @Deprecated
    public GsonConfigInstance(Class<T> configClass, Path path, UnaryOperator<GsonBuilder> builder) {
        this(configClass, path, builder.apply(new GsonBuilder()));
    }

    @Deprecated
    public GsonConfigInstance(Class<T> configClass, Path path, GsonBuilder builder) {
        super(configClass);
        this.path = path;
        this.gson = builder
                .setExclusionStrategies(new ConfigExclusionStrategy())
                .registerTypeHierarchyAdapter(Component.class, new Component.SerializerAdapter())
                .registerTypeHierarchyAdapter(Style.class, new GsonConfigSerializer.StyleTypeAdapter())
                .registerTypeHierarchyAdapter(Color.class, new ColorTypeAdapter())
                .registerTypeHierarchyAdapter(Item.class, new ItemTypeAdapter())
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    private GsonConfigInstance(Class<T> configClass, Path path, Gson gson, boolean fromBuilder) {
        super(configClass);
        this.path = path;
        this.gson = gson;
    }

    @Override
    public void save() {
        try {
            YACLConstants.LOGGER.info("Saving {}...", getConfigClass().getSimpleName());
            Files.writeString(path, gson.toJson(getConfig()), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        try {
            if (Files.notExists(path)) {
                save();
                return;
            }

            YACLConstants.LOGGER.info("Loading {}...", getConfigClass().getSimpleName());
            setConfig(gson.fromJson(Files.readString(path), getConfigClass()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Path getPath() {
        return this.path;
    }

    private static class ConfigExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getAnnotation(ConfigEntry.class) == null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return false;
        }
    }

    public static class ColorTypeAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
        @Override
        public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new Color(jsonElement.getAsInt(), true);
        }

        @Override
        public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(color.getRGB());
        }
    }
    public static class ItemTypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
        @Override
        public Item deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return ItemRegistryHelper.getItemFromName(jsonElement.getAsString());
        }

        @Override
        public JsonElement serialize(Item item, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(BuiltInRegistries.ITEM.getKey(item).toString());
        }
    }

    /**
     * Creates a builder for a GSON config instance.
     * @param configClass the config class
     * @return a new builder
     * @param <T> the config type
     */
    public static <T> Builder<T> createBuilder(Class<T> configClass) {
        return new Builder<>(configClass);
    }

    public static class Builder<T> {
        private final Class<T> configClass;
        private Path path;
        private UnaryOperator<GsonBuilder> gsonBuilder = builder -> builder
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                .registerTypeHierarchyAdapter(Component.class, new Component.SerializerAdapter())
                .registerTypeHierarchyAdapter(Style.class, new GsonConfigSerializer.StyleTypeAdapter())
                .registerTypeHierarchyAdapter(Color.class, new ColorTypeAdapter())
                .registerTypeHierarchyAdapter(Item.class, new ItemTypeAdapter());

        private Builder(Class<T> configClass) {
            this.configClass = configClass;
        }

        /**
         * Sets the file path to save and load the config from.
         */
        public Builder<T> setPath(Path path) {
            this.path = path;
            return this;
        }

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
        public Builder<T> overrideGsonBuilder(GsonBuilder gsonBuilder) {
            this.gsonBuilder = builder -> gsonBuilder;
            return this;
        }

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
        public Builder<T> overrideGsonBuilder(Gson gson) {
            return this.overrideGsonBuilder(gson.newBuilder());
        }

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
        public Builder<T> appendGsonBuilder(UnaryOperator<GsonBuilder> gsonBuilder) {
            UnaryOperator<GsonBuilder> prev = this.gsonBuilder;
            this.gsonBuilder = builder -> gsonBuilder.apply(prev.apply(builder));
            return this;
        }

        /**
         * Builds the config instance.
         * @return the built config instance
         */
        public GsonConfigInstance<T> build() {
            UnaryOperator<GsonBuilder> gsonBuilder = builder -> this.gsonBuilder.apply(builder)
                    .addSerializationExclusionStrategy(new ConfigExclusionStrategy())
                    .addDeserializationExclusionStrategy(new ConfigExclusionStrategy());

            return new GsonConfigInstance<>(configClass, path, gsonBuilder.apply(new GsonBuilder()).create(), true);
        }
    }
}
