package dev.isxander.yacl3.config.v3;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;
import java.util.function.UnaryOperator;

@ApiStatus.Experimental
public abstract class AbstractReadonlyConfigEntry<T> implements ReadonlyConfigEntry<T> {
    private final String fieldName;

    private Function<T, T> getModifier;

    public AbstractReadonlyConfigEntry(String fieldName) {
        this.fieldName = fieldName;
        this.getModifier = UnaryOperator.identity();
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public T get() {
        return this.getModifier.apply(this.innerGet());
    }

    protected abstract T innerGet();

    @Override
    public ReadonlyConfigEntry<T> modifyGet(UnaryOperator<T> modifier) {
        this.getModifier = this.getModifier.andThen(modifier);
        return this;
    }

}
