package dev.isxander.yacl3.config.v3;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.RecordBuilder;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Experimental
public class ChildConfigEntryImpl<T extends CodecConfig<T>> extends AbstractReadonlyConfigEntry<T> {
    private final T config;
    private final MapCodec<T> mapCodec;

    public ChildConfigEntryImpl(String fieldName, T config) {
        super(fieldName);
        this.config = config;
        this.mapCodec = config.fieldOf(this.fieldName());
    }

    @Override
    protected T innerGet() {
        return config;
    }

    @Override
    public <R> RecordBuilder<R> encode(DynamicOps<R> ops, RecordBuilder<R> recordBuilder) {
        return mapCodec.encode(config, ops, recordBuilder);
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

        return true;
    }
}
