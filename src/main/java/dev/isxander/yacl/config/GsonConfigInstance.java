package dev.isxander.yacl.config;

import com.google.gson.*;
import dev.isxander.yacl.impl.utils.YACLConstants;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.UnaryOperator;

public class GsonConfigInstance<T> extends ConfigInstance<T> {
    private final Gson gson;
    private final Path path;

    public GsonConfigInstance(Class<T> configClass, Path path, Gson gson) {
        this(configClass, path, gson.newBuilder());
    }

    public GsonConfigInstance(Class<T> configClass, Path path, UnaryOperator<GsonBuilder> builder) {
        this(configClass, path, builder.apply(new GsonBuilder()));
    }

    public GsonConfigInstance(Class<T> configClass, Path path, GsonBuilder builder) {
        super(configClass);
        this.path = path;
        this.gson = builder
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
                .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
                .registerTypeHierarchyAdapter(Color.class, new ColorTypeAdapter())
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
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
}
