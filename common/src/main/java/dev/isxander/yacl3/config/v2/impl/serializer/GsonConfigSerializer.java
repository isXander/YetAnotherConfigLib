package dev.isxander.yacl3.config.v2.impl.serializer;

import com.google.gson.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.ConfigSerializer;
import dev.isxander.yacl3.config.v2.api.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.awt.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class GsonConfigSerializer<T> extends ConfigSerializer<T> {
    private final Gson gson;
    private final Path path;

    private GsonConfigSerializer(ConfigClassHandler<T> config, Path path, Gson gson) {
        super(config);
        this.gson = gson;
        this.path = path;
    }

    @Override
    public void serialize() {
        JsonObject root = new JsonObject();

        for (ConfigField<?> field : config.fields()) {
            if (YACLPlatform.isDevelopmentEnv() && field.comment().isPresent()) {
                YACLConstants.LOGGER.error("Config field '{}' has a comment, but comments are not supported by Gson. Please remove the comment or switch to a different serializer. This log will not be shown in production.", field.serialName());
            }

            try {
                root.add(field.serialName(), gson.toJsonTree(field.access().get()));
            } catch (Exception e) {
                YACLConstants.LOGGER.error("Failed to serialize config field '{}'.", field.serialName(), e);
            }
        }

        YACLConstants.LOGGER.info("Serializing {} to '{}'", config.configClass(), path);
        try {
            Files.writeString(path, gson.toJson(root), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (Exception e) {
            YACLConstants.LOGGER.error("Failed to serialize config class '{}'.", config.configClass().getSimpleName(), e);
        }
    }

    @Override
    public void deserialize() {
        if (!Files.exists(path)) {
            YACLConstants.LOGGER.info("Config file '{}' does not exist. Creating it with default values.", path);
            serialize();
            return;
        }

        YACLConstants.LOGGER.info("Deserializing {} from '{}'", config.configClass().getSimpleName(), path);

        String json;
        try {
            json = Files.readString(path);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read '%s' for deserialization.".formatted(path), e);
        }

        Map<String, JsonElement> root = gson.fromJson(json, JsonObject.class).asMap();
        List<String> unconsumedKeys = new ArrayList<>(root.keySet());

        for (ConfigField<?> field : config.fields()) {
            if (root.containsKey(field.serialName())) {
                try {
                    field.access().set(gson.fromJson(root.get(field.serialName()), field.access().type()));
                } catch (Exception e) {
                    YACLConstants.LOGGER.error("Failed to deserialize config field '{}'.", field.serialName(), e);
                }
            } else {
                YACLConstants.LOGGER.warn("Config field '{}' was not found in the config file. Skipping.", field.serialName());
            }

            unconsumedKeys.remove(field.serialName());
        }

        if (!unconsumedKeys.isEmpty()) {
            YACLConstants.LOGGER.warn("The following keys were not consumed by the config class: {}", String.join(", ", unconsumedKeys));
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

    public static class Builder<T> implements GsonConfigSerializerBuilder<T> {
        private final ConfigClassHandler<T> config;
        private Path path;
        private UnaryOperator<GsonBuilder> gsonBuilder = builder -> builder
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                .registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
                .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
                .registerTypeHierarchyAdapter(Color.class, new ColorTypeAdapter())
                .setPrettyPrinting();

        public Builder(ConfigClassHandler<T> config) {
            this.config = config;
        }

        @Override
        public Builder<T> setPath(Path path) {
            this.path = path;
            return this;
        }

        @Override
        public Builder<T> overrideGsonBuilder(GsonBuilder gsonBuilder) {
            this.gsonBuilder = builder -> gsonBuilder;
            return this;
        }

        @Override
        public Builder<T> overrideGsonBuilder(Gson gson) {
            return this.overrideGsonBuilder(gson.newBuilder());
        }

        @Override
        public Builder<T> appendGsonBuilder(UnaryOperator<GsonBuilder> gsonBuilder) {
            UnaryOperator<GsonBuilder> prev = this.gsonBuilder;
            this.gsonBuilder = builder -> gsonBuilder.apply(prev.apply(builder));
            return this;
        }

        @Override
        public GsonConfigSerializer<T> build() {
            return new GsonConfigSerializer<>(config, path, gsonBuilder.apply(new GsonBuilder()).create());
        }
    }
}
