package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionFlag;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class OptionImpl<T> implements Option<T> {
    private final Text name;
    private Text tooltip;
    private final Controller<T> controller;
    private final Binding<T> binding;
    private boolean available;

    private final ImmutableSet<OptionFlag> flags;

    private final Class<T> typeClass;

    private T pendingValue;

    private final List<BiConsumer<Option<T>, T>> listeners;

    public OptionImpl(
            @NotNull Text name,
            @Nullable Function<T, Text> tooltipGetter,
            @NotNull Function<Option<T>, Controller<T>> controlGetter,
            @NotNull Binding<T> binding,
            boolean available,
            ImmutableSet<OptionFlag> flags,
            @NotNull Class<T> typeClass,
            @NotNull Collection<BiConsumer<Option<T>, T>> listeners
    ) {
        this.name = name;
        this.binding = binding;
        this.available = available;
        this.flags = flags;
        this.typeClass = typeClass;
        this.listeners = new ArrayList<>(listeners);
        this.controller = controlGetter.apply(this);

        addListener((opt, pending) -> tooltip = tooltipGetter.apply(pending));
        requestSet(binding().getValue());
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
    public @NotNull Binding<T> binding() {
        return binding;
    }

    @Override
    public boolean available() {
        return available;
    }

    @Override
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public @NotNull Class<T> typeClass() {
        return typeClass;
    }

    @Override
    public @NotNull ImmutableSet<OptionFlag> flags() {
        return flags;
    }

    @Override
    public boolean requiresRestart() {
        return flags.contains(OptionFlag.GAME_RESTART);
    }

    @Override
    public boolean changed() {
        return !binding().getValue().equals(pendingValue);
    }

    @Override
    public @NotNull T pendingValue() {
        return pendingValue;
    }

    @Override
    public void requestSet(T value) {
        pendingValue = value;
        listeners.forEach(listener -> listener.accept(this, pendingValue));
    }

    @Override
    public boolean applyValue() {
        if (changed()) {
            binding().setValue(pendingValue);
            return true;
        }
        return false;
    }

    @Override
    public void forgetPendingValue() {
        requestSet(binding().getValue());
    }

    @Override
    public void requestSetDefault() {
        requestSet(binding().defaultValue());
    }

    @Override
    public boolean isPendingValueDefault() {
        return binding().defaultValue().equals(pendingValue());
    }

    @Override
    public void addListener(BiConsumer<Option<T>, T> changedListener) {
        this.listeners.add(changedListener);
    }
}
