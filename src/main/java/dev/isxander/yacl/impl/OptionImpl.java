package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.Storage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@ApiStatus.Internal
public class OptionImpl<T, S> implements Option<T, S> {
    private final Text name;
    private final Text tooltip;
    private final Controller<T> controller;
    private final Binding<T, S> binding;
    private final Storage<S> storage;
    private final boolean requiresRestart;

    private final Class<T> typeClass;

    private T pendingValue;

    public OptionImpl(
            @NotNull Text name,
            @Nullable Text tooltip,
            @NotNull Function<Option<T, S>, Controller<T>> controlGetter,
            @NotNull Binding<T, S> binding,
            @NotNull Storage<S> storage,
            boolean requiresRestart,
            @NotNull Class<T> typeClass
    ) {
        this.name = name;
        this.tooltip = tooltip;
        this.controller = controlGetter.apply(this);
        this.binding = binding;
        this.storage = storage;
        this.requiresRestart = requiresRestart;
        this.typeClass = typeClass;
        this.pendingValue = binding().getValue(storage.data());
    }

    @Override
    public @NotNull Text name() {
        return name;
    }

    @Override
    public @NotNull Text tooltip() {
        return tooltip;
    }

    @Override
    public @NotNull Controller<T> controller() {
        return controller;
    }

    @Override
    public @NotNull Binding<T, S> binding() {
        return binding;
    }

    @Override
    public @NotNull Storage<S> storage() {
        return storage;
    }

    @Override
    public @NotNull Class<T> typeClass() {
        return typeClass;
    }

    @Override
    public boolean requiresRestart() {
        return requiresRestart;
    }

    @Override
    public boolean changed() {
        return !binding().getValue(storage().data()).equals(pendingValue);
    }

    @Override
    public @NotNull T pendingValue() {
        return pendingValue;
    }

    @Override
    public void requestSet(T value) {
        pendingValue = value;
    }

    @Override
    public boolean applyValue() {
        if (changed()) {
            binding().setValue(storage().data(), pendingValue);
            return true;
        }
        return false;
    }

    @Override
    public void forgetPendingValue() {
        pendingValue = binding().getValue(storage().data());
    }

    @Override
    public void requestSetDefault() {
        pendingValue = binding().defaultValue(storage().data());
    }
}
