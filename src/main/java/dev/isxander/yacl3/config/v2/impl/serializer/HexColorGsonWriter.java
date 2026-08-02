package dev.isxander.yacl3.config.v2.impl.serializer;

import org.quiltmc.parsers.json.JsonWriter;
import org.quiltmc.parsers.json.gson.GsonWriter;

import java.io.IOException;

public class HexColorGsonWriter extends GsonWriter {
    private final boolean json5;

    public HexColorGsonWriter(JsonWriter writer, boolean json5) {
        super(writer);
        this.json5 = json5;
    }

    @Override
    public com.google.gson.stream.JsonWriter value(Number value) throws IOException {
        if (!(value instanceof HexColorNumber hexColorNumber))
            return super.value(value);

        if (json5)
            return jsonValue(hexColorNumber.toString());
        else
            return value(hexColorNumber.toString());
    }
}
