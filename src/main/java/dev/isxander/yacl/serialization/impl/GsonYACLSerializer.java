package dev.isxander.yacl.serialization.impl;

import com.google.gson.*;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.impl.YACLConstants;
import dev.isxander.yacl.serialization.IYACLSerializer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonYACLSerializer implements IYACLSerializer {
    private final YetAnotherConfigLib yacl;
    private final Gson gson;
    private final Path filePath;

    public GsonYACLSerializer(YetAnotherConfigLib yacl, Path filePath) {
        this(yacl, filePath, new GsonBuilder().setPrettyPrinting());
    }

    public GsonYACLSerializer(YetAnotherConfigLib yacl, Path filePath, Gson gson) {
        this(yacl, filePath, gson.newBuilder());
    }

    public GsonYACLSerializer(YetAnotherConfigLib yacl, Path filePath, GsonBuilder gsonBuilder) {
        this.yacl = yacl;
        this.filePath = filePath;
        gsonBuilder
                .disableHtmlEscaping()
                .registerTypeAdapter(Color.class, new ColorSerializer())
                .registerTypeHierarchyAdapter(Text.class, new Text.Serializer())
                .registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
        this.gson = gsonBuilder.create();
    }

    @Override
    public void save() {
        YACLConstants.LOGGER.info("Saving {}", yacl.title().getString());

        JsonObject rootObj = new JsonObject();
        for (ConfigCategory category : yacl.categories()) {
            JsonObject categoryObj = new JsonObject();

            for (OptionGroup group : category.groups()) {
                JsonObject groupObj;
                if (group.isRoot()) groupObj = categoryObj;
                else groupObj = new JsonObject();

                for (Option<?> option : group.options()) {
                    if (option instanceof ButtonOption)
                        continue;

                    groupObj.add(getKey(option.name()), gson.toJsonTree(option.binding().getValue()));
                }

                if (!group.isRoot())
                    categoryObj.add(getKey(group.name()), groupObj);
            }

            rootObj.add(getKey(category.name()), categoryObj);
        }

        try {
            Files.deleteIfExists(filePath);
            Files.writeString(filePath, gson.toJson(rootObj));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load() {
        YACLConstants.LOGGER.info("Loading {}", yacl.title().getString());

        if (Files.notExists(filePath)) {
            save();
            return;
        }

        boolean shouldSave = false;
        JsonObject rootObj;
        try {
            rootObj = gson.fromJson(Files.readString(filePath), JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (ConfigCategory category : yacl.categories()) {
            String categoryKey = getKey(category.name());
            if (!rootObj.has(categoryKey)) {
                YACLConstants.LOGGER.warn("JSON for '{}' did not have expected object '{}'! Re-saving once loaded.", yacl.title().getString(), categoryKey);
                shouldSave = true;
                continue;
            }

            JsonObject categoryObj = rootObj.getAsJsonObject(categoryKey);

            for (OptionGroup group : category.groups()) {
                JsonObject groupObj;
                if (group.isRoot()) groupObj = categoryObj;
                else {
                    String groupKey = getKey(group.name());
                    if (!categoryObj.has(groupKey)) {
                        YACLConstants.LOGGER.warn("JSON for '{}' did not have expected object '{}'! Re-saving once loaded.", yacl.title().getString(), groupKey);
                        shouldSave = true;
                        continue;
                    }

                    groupObj = categoryObj.getAsJsonObject(groupKey);
                }

                for (Option<?> option : group.options()) {
                    String optionKey = getKey(option.name());
                    if (!groupObj.has(optionKey)) {
                        YACLConstants.LOGGER.warn("JSON for '{}' did not have expected element '{}'! Re-saving once loaded.", yacl.title().getString(), optionKey);
                        shouldSave = true;
                        continue;
                    }

                    if (option instanceof ButtonOption)
                        continue;

                    option.binding().setValue(gson.fromJson(groupObj.get(optionKey), (Type) option.typeClass()));
                }
            }
        }

        if (shouldSave) {
            save();
        }
    }

    private String getKey(Text text) {
        if (text.getContent() instanceof TranslatableTextContent translatableTextContent) {
            return translatableTextContent.getKey();
        }

        return text.getString().toLowerCase().replace(' ', '_');
    }

    private static class ColorSerializer implements JsonSerializer<Color>, JsonDeserializer<Color> {
        @Override
        public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getRGB());
        }

        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Color(json.getAsInt(), true);
        }
    }
}
