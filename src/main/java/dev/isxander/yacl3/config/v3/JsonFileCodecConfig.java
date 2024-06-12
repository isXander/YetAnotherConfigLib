package dev.isxander.yacl3.config.v3;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@ApiStatus.Experimental
public abstract class JsonFileCodecConfig<T extends JsonFileCodecConfig<T>> extends CodecConfig<T> {
    private final Path configPath;
    private final Gson gson;

    public JsonFileCodecConfig(Path configPath) {
        this.configPath = configPath;
        this.gson = createGson();
    }

    public void saveToFile() {
        DataResult<JsonElement> jsonTreeResult = this.encodeStart(JsonOps.INSTANCE);
        if (jsonTreeResult.error().isPresent()) {
            onSaveError(
                    SaveError.ENCODING,
                    new IllegalStateException("Failed to encode: " + jsonTreeResult.error().get().message())
            );
            return;
        }

        JsonElement jsonTree = jsonTreeResult.result().orElseThrow();
        String json = gson.toJson(jsonTree);

        try {
            Files.writeString(configPath, json, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            onSaveError(SaveError.WRITING, e);
        }
    }

    public boolean loadFromFile() {
        if (Files.notExists(configPath)) {
            return false;
        }

        String json;
        try {
            json = Files.readString(configPath);
        } catch (IOException e) {
            onLoadError(LoadError.READING, e);
            return false;
        }

        JsonElement jsonTree;
        try {
            jsonTree = JsonParser.parseString(json);
        } catch (JsonParseException e) {
            onLoadError(LoadError.JSON_PARSING, e);
            return false;
        }

        return this.decode(jsonTree, JsonOps.INSTANCE);
    }

    protected Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    protected void onSaveError(SaveError error, @Nullable Throwable e) {
        throw new IllegalStateException("Error whilst " + error.name().toLowerCase(), e);
    }

    protected void onLoadError(LoadError error, @Nullable Throwable e) {
        throw new IllegalStateException("Error whilst " + error.name().toLowerCase(), e);
    }

    protected enum SaveError {
        WRITING,
        ENCODING,
    }

    protected enum LoadError {
        READING,
        JSON_PARSING,
        DECODING,
    }
}
