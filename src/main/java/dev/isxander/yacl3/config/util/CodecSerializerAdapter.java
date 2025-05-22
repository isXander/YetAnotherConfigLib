package dev.isxander.yacl3.config.util;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.lang.reflect.Type;

public class CodecSerializerAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    private final Codec<T> codec;

    public CodecSerializerAdapter(Codec<T> codec) {
        this.codec = codec;
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        return codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow();
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return codec.parse(JsonOps.INSTANCE, json).getOrThrow();
    }
}
