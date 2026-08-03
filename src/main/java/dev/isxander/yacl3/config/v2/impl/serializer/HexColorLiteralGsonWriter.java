package dev.isxander.yacl3.config.v2.impl.serializer;

import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonWriter;

import java.io.IOException;

public class HexColorLiteralGsonWriter extends GsonWriter {
    public HexColorLiteralGsonWriter(JsonWriter writer) {
        super(writer);
    }

    @Override
    public com.google.gson.stream.JsonWriter value(Number value) throws IOException {
        return value instanceof HexColorNumber hexColorNumber
                ? jsonValue(hexColorNumber.hex())
                : super.value(value);
    }
}
