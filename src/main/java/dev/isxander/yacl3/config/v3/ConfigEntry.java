package dev.isxander.yacl3.config.v3;

import dev.isxander.yacl3.api.Binding;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

@ApiStatus.Experimental
public interface ConfigEntry<T> extends ReadonlyConfigEntry<T> {

    void set(T value);

    T defaultValue();

    @Override
    ConfigEntry<T> modifyGet(UnaryOperator<T> modifier);

    @Override
    default ConfigEntry<T> onGet(Consumer<T> consumer) {
        return this.modifyGet(v -> {
            consumer.accept(v);
            return v;
        });
    }

    ConfigEntry<T> modifySet(UnaryOperator<T> modifier);
    default ConfigEntry<T> onSet(Consumer<T> consumer) {
        return this.modifySet(v -> {
            consumer.accept(v);
            return v;
        });
    }

    default Binding<T> asBinding() {
        return Binding.generic(this.defaultValue(), this::get, this::set);
    }
}
