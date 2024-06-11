package dev.isxander.yacl3.config.v3;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@ApiStatus.Experimental
public interface ReadonlyConfigEntry<T> {
    String fieldName();

    T get();

    ReadonlyConfigEntry<T> modifyGet(UnaryOperator<T> modifier);
    default ReadonlyConfigEntry<T> onGet(Consumer<T> consumer) {
        return this.modifyGet(v -> {
            consumer.accept(v);
            return v;
        });
    }

    <R> RecordBuilder<R> encode(DynamicOps<R> ops, RecordBuilder<R> recordBuilder);
    <R> boolean decode(R encoded, DynamicOps<R> ops);
}
