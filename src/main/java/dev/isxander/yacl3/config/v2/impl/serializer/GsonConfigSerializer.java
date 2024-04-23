package dev.isxander.yacl3.config.v2.impl.serializer;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import dev.isxander.yacl3.config.v2.api.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonReader;
import org.quiltmc.parsers.json.gson.GsonWriter;

import java.awt.Color;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
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
    public void save() {
        YACLConstants.LOGGER.info("Serializing {} to '{}'", config.configClass(), path);

        try (StringWriter stringWriter = new StringWriter()) {
            JsonWriter jsonWriter = json5 ? JsonWriter.json5(stringWriter) : JsonWriter.json(stringWriter);
            GsonWriter gsonWriter = new GsonWriter(jsonWriter);

            jsonWriter.beginObject();

            for (ConfigField<?> field : config.fields()) {
                SerialField serial = field.serial().orElse(null);
                if (serial == null) continue;

                if (!json5 && serial.comment().isPresent() && YACLPlatform.isDevelopmentEnv()) {
                    YACLConstants.LOGGER.warn("Found comment in config field '{}', but json5 is not enabled. Enable it with `.setJson5(true)` on the `GsonConfigSerializerBuilder`. Comments will not be serialized. This warning is only visible in development environments.", serial.serialName());
                }
                jsonWriter.comment(serial.comment().orElse(null));

                jsonWriter.name(serial.serialName());

                JsonElement element;
                try {
                    element = gson.toJsonTree(field.access().get(), field.access().type());
                } catch (Exception e) {
                    YACLConstants.LOGGER.error("Failed to serialize config field '{}'. Serializing as null.", serial.serialName(), e);
                    jsonWriter.nullValue();
                    continue;
                }

                try {
                    gson.toJson(element, gsonWriter);
                } catch (Exception e) {
                    YACLConstants.LOGGER.error("Failed to serialize config field '{}'. Due to the error state this JSON writer cannot continue safely and the save will be abandoned.", serial.serialName(), e);
                    return;
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
    public LoadResult loadSafely(Map<ConfigField<?>, FieldAccess<?>> bufferAccessMap) {
        if (!Files.exists(path)) {
            YACLConstants.LOGGER.info("Config file '{}' does not exist. Creating it with default values.", path);
            save();
            return LoadResult.NO_CHANGE;
        }

        YACLConstants.LOGGER.info("Deserializing {} from '{}'", config.configClass().getSimpleName(), path);

        Map<String, ConfigField<?>> fieldMap = Arrays.stream(config.fields())
                .filter(field -> field.serial().isPresent())
                .collect(Collectors.toMap(f -> f.serial().orElseThrow().serialName(), Function.identity()));
        Set<String> missingFields = fieldMap.keySet();
        boolean dirty = false;

        try (JsonReader jsonReader = json5 ? JsonReader.json5(path) : JsonReader.json(path)) {
            GsonReader gsonReader = new GsonReader(jsonReader);

            jsonReader.beginObject();

            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                ConfigField<?> field = fieldMap.get(name);
                missingFields.remove(name);

                if (field == null) {
                    YACLConstants.LOGGER.warn("Found unknown config field '{}'.", name);
                    jsonReader.skipValue();
                    continue;
                }

                FieldAccess<?> bufferAccess = bufferAccessMap.get(field);
                SerialField serial = field.serial().orElse(null);
                if (serial == null) continue;

                JsonElement element;
                try {
                    element = gson.fromJson(gsonReader, JsonElement.class);
                } catch (Exception e) {
                    YACLConstants.LOGGER.error("Failed to deserialize config field '{}'. Due to the error state this JSON reader cannot be re-used and loading will be aborted.", name, e);
                    return LoadResult.FAILURE;
                }

                if (element.isJsonNull() && !serial.nullable()) {
                    YACLConstants.LOGGER.warn("Found null value in non-nullable config field '{}'. Leaving field as default and marking as dirty.", name);
                    dirty = true;
                    continue;
                }

                try {
                    bufferAccess.set(gson.fromJson(element, bufferAccess.type()));
                } catch (Exception e) {
                    YACLConstants.LOGGER.error("Failed to deserialize config field '{}'. Leaving as default.", name, e);
                }
            }

            jsonReader.endObject();
        } catch (IOException e) {
            YACLConstants.LOGGER.error("Failed to deserialize config class.", e);
            return LoadResult.FAILURE;
        }

        if (!missingFields.isEmpty()) {
            for (String missingField : missingFields) {
                if (fieldMap.get(missingField).serial().orElseThrow().required()) {
                    dirty = true;
                    YACLConstants.LOGGER.warn("Missing required config field '{}''. Re-saving as default.", missingField);
                }
            }
        }

        return dirty ? LoadResult.DIRTY : LoadResult.SUCCESS;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void load() {
        YACLConstants.LOGGER.warn("Calling ConfigSerializer#load() directly is deprecated. Please use ConfigClassHandler#load() instead.");
        config.load();
    }

    /*? if >=1.20.4 {*/
    public static class StyleTypeAdapter implements JsonSerializer<Style>, JsonDeserializer<Style> {
        @Override
        public Style deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Style.Serializer.CODEC.parse(JsonOps.INSTANCE, json).result().orElse(Style.EMPTY);
        }

        @Override
        public JsonElement serialize(Style src, Type typeOfSrc, JsonSerializationContext context) {
            return Style.Serializer.CODEC.encodeStart(JsonOps.INSTANCE, src).result().orElse(JsonNull.INSTANCE);
        }
    }
    /*?}*/

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

    @ApiStatus.Internal
    public static class Builder<T> implements GsonConfigSerializerBuilder<T> {
        private final ConfigClassHandler<T> config;
        private Path path;
        private boolean json5;
        private UnaryOperator<GsonBuilder> gsonBuilder = builder -> builder
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .serializeNulls()
                /*? if >1.20.4 { */
                .registerTypeHierarchyAdapter(Component.class, new Component.SerializerAdapter(RegistryAccess.EMPTY))
                /*? } elif =1.20.4 {*//*
                .registerTypeHierarchyAdapter(Component.class, new Component.SerializerAdapter())
                *//*? } else {*//*
                .registerTypeHierarchyAdapter(Component.class, new Component.Serializer())
                *//*?}*/
                .registerTypeHierarchyAdapter(Style.class, /*? if >=1.20.4 {*/new StyleTypeAdapter()/*?} else {*//*new Style.Serializer()*//*?}*/)
                .registerTypeHierarchyAdapter(Color.class, new ColorTypeAdapter())
                .registerTypeHierarchyAdapter(Item.class, new ItemTypeAdapter())
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
