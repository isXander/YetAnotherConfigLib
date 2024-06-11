package dev.isxander.yacl3.config.v3;

import com.mojang.serialization.*;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Experimental
public class CodecConfigEntryImpl<T> extends AbstractConfigEntry<T> {
    private final MapCodec<T> mapCodec;

    public CodecConfigEntryImpl(String fieldName, T defaultValue, Codec<T> codec) {
        super(fieldName, defaultValue);
        this.mapCodec = codec.fieldOf(this.fieldName());
    }

    @Override
    public <R> RecordBuilder<R> encode(DynamicOps<R> ops, RecordBuilder<R> recordBuilder) {
        return mapCodec.encode(get(), ops, recordBuilder);
    }

    @Override
    public <R> boolean decode(R encoded, DynamicOps<R> ops) {
        DataResult<T> result = mapCodec.decoder().parse(ops, encoded);

        //? if >1.20.4 {
        Optional<DataResult.Error<T>> error = result.error();
         //?} else {
        /*Optional<DataResult.PartialResult<T>> error = result.error();
        *///?}
        if (error.isPresent()) {
            YACLConstants.LOGGER.error("Failed to decode entry {}: {}", this.fieldName(), error.get().message());
            return false;
        }

        T value = result.result().orElseThrow();
        this.set(value);

        return true;
    }
}
