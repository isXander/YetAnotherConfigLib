package dev.isxander.yacl3.config.v3;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Experimental
public abstract class CodecConfig<S extends CodecConfig<S>> implements EntryAddable, Codec<S> {
    private final List<ReadonlyConfigEntry<?>> entries = new ArrayList<>();

    public CodecConfig() {
        // cast here to throw immediately on construction
        var ignored = (S) this;
    }

    @Override
    public <T> ConfigEntry<T> register(String fieldName, T defaultValue, Codec<T> codec) {
        ConfigEntry<T> entry = new CodecConfigEntryImpl<>(fieldName, defaultValue, codec);
        entries.add(entry);
        return entry;
    }

    @Override
    public <T extends CodecConfig<T>> ReadonlyConfigEntry<T> register(String fieldName, T configInstance) {
        ReadonlyConfigEntry<T> entry = new ChildConfigEntryImpl<>(fieldName, configInstance);
        entries.add(entry);
        return entry;
    }

    protected void onFinishedDecode(boolean successful) {
    }

    @Override
    public <R> DataResult<R> encode(S input, DynamicOps<R> ops, R prefix) {
        if (input != null && input != this) {
            throw new IllegalArgumentException("`input` is ignored. It must be null or equal to `this`.");
        }

        return this.encode(ops, prefix);
    }

    @Override
    public <R> DataResult<Pair<S, R>> decode(DynamicOps<R> ops, R input) {
        this.decode(input, ops);
        return DataResult.success(Pair.of((S) this, input));
    }

    public final <R> DataResult<R> encode(DynamicOps<R> ops, R prefix) {
        RecordBuilder<R> builder = ops.mapBuilder();
        for (ReadonlyConfigEntry<?> entry : entries) {
            builder = entry.encode(ops, builder);
        }
        return builder.build(prefix);
    }

    public final <R> DataResult<R> encodeStart(DynamicOps<R> ops) {
        return this.encode(ops, ops.empty());
    }

    /**
     * @return true if decoding of all entries was successful
     */
    public final <R> boolean decode(R encoded, DynamicOps<R> ops) {
        boolean success = true;

        for (ReadonlyConfigEntry<?> entry : entries) {
            success &= entry.decode(encoded, ops);
        }

        onFinishedDecode(success);

        return success;
    }
}
