package dev.isxander.yacl3.config.v3;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;
import java.util.function.UnaryOperator;

@ApiStatus.Experimental
public abstract class AbstractConfigEntry<T> extends AbstractReadonlyConfigEntry<T> implements ConfigEntry<T> {
    private T value;
    private final T defaultValue;

    private Function<T, T> setModifier;

    public AbstractConfigEntry(String fieldName, T defaultValue) {
        super(fieldName);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.setModifier = UnaryOperator.identity();
    }

    @Override
    protected T innerGet() {
        return this.value;
    }

    @Override
    public void set(T value) {
        this.value = this.setModifier.apply(value);
    }

    @Override
    public T defaultValue() {
        return this.defaultValue;
    }

    @Override
    public ConfigEntry<T> modifyGet(UnaryOperator<T> modifier) {
        super.modifyGet(modifier);
        return this;
    }

    @Override
    public ConfigEntry<T> modifySet(UnaryOperator<T> modifier) {
        this.setModifier = this.setModifier.andThen(modifier);
        return this;
    }
}
