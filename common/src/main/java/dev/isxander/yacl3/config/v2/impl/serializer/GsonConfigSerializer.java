package dev.isxander.yacl3.config.v2.impl.serializer;

import com.google.gson.*;
import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.quiltmc.parsers.json.gson.GsonWriter;

import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class GsonConfigSerializer<T> extends ConfigSerializer<T> {
    private final Gson gson;
    private final Path path;
    private final boolean json5;

    private GsonConfigSerializer(ConfigClassHandler<T> config, Path path, Gson gson, boolean json5) {
        super(config);
        this.gson = gson;
        this.path = path;
        this.json5 = json5;
    }

    @Override
    public void serialize() {
        YACLConstants.LOGGER.info("Serializing {} to '{}'", config.configClass(), path);

        try (StringWriter stringWriter = new StringWriter()) {
            JsonWriter jsonWriter = json5 ? JsonWriter.json5(stringWriter) : JsonWriter.json(stringWriter);
            GsonWriter gsonWriter = new GsonWriter(jsonWriter);
            jsonWriter.beginObject();
            for (ConfigField<?> field : config.fields()) {
                SerialField serial = field.serial().orElse(null);
                if (serial == null) {
                    continue;
                }

                if (!json5 && serial.comment().isPresent() && YACLPlatform.isDevelopmentEnv()) {
                    YACLConstants.LOGGER.warn("Found comment in config field '{}', but json5 is not enabled. Enable it with `.setJson5(true)` on the `GsonConfigSerializerBuilder`. Comments will not be serialized. This warning is only visible in development environments.", serial.serialName());
                }
                jsonWriter.comment(serial.comment().orElse(null));

                jsonWriter.name(serial.serialName());
                try {
                    gson.toJson(field.access().get(), field.access().type(), gsonWriter);
                } catch (Exception e) {
                    YACLConstants.LOGGER.error("Failed to serialize config field '{}'.", serial.serialName(), e);
                    jsonWriter.nullValue();
                }
            }
            jsonWriter.endObject();
            jsonWriter.flush();

            Files.createDirectories(path.getParent());
            Files.writeString(path, stringWriter.toString(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
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

        try (JsonReader jsonReader = json5 ? JsonReader.json5(path) : JsonReader.json(path)) {
            GsonReader gsonReader = new GsonReader(jsonReader);

            Map<String, ConfigField<?>> fieldMap = Arrays.stream(config.fields())
                    .filter(field -> field.serial().isPresent())
                    .collect(Collectors.toMap(f -> f.serial().orElseThrow().serialName(), Function.identity()));

            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                ConfigField<?> field = fieldMap.get(name);
                if (field == null) {
                    YACLConstants.LOGGER.warn("Found unknown config field '{}' in '{}'.", name, path);
                    jsonReader.skipValue();
                    continue;
                }

                try {
                    field.access().set(gson.fromJson(gsonReader, field.access().type()));
                } catch (Exception e) {
                    YACLConstants.LOGGER.error("Failed to deserialize config field '{}'.", name, e);
                    jsonReader.skipValue();
                }
            }

            jsonReader.endObject();
        } catch (IOException e) {
            YACLConstants.LOGGER.error("Failed to deserialize config class '{}'.", config.configClass().getSimpleName(), e);
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
        private boolean json5;
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
        public Builder<T> setJson5(boolean json5) {
            this.json5 = json5;
            return this;
        }

        @Override
        public GsonConfigSerializer<T> build() {
            return new GsonConfigSerializer<>(config, path, gsonBuilder.apply(new GsonBuilder()).create(), json5);
        }
    }
}
