package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ListOptionEntryImpl<T> implements ListOptionEntry<T> {
    private final ListOption<T> group;

    private T value;

    private final Binding<T> binding;
    private final Controller<T> controller;

    public ListOptionEntryImpl(ListOption<T> group, T initialValue, @NotNull Function<ListOptionEntry<T>, Controller<T>> controlGetter) {
        this.group = group;
        this.value = initialValue;
        this.binding = new EntryBinding();
        this.controller = controlGetter.apply(this);
    }

    @Override
    public @NotNull Text name() {
        return Text.empty();
    }

    @Override
    public @NotNull Text tooltip() {
        return Text.empty();
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
        return true;
    }

    @Override
    public void setAvailable(boolean available) {

    }

    @Override
    public ListOption<T> parentGroup() {
        return group;
    }

    @Override
    public boolean changed() {
        return false;
    }

    @Override
    public @NotNull T pendingValue() {
        return value;
    }

    @Override
    public void requestSet(T value) {
        binding.setValue(value);
    }

    @Override
    public boolean applyValue() {
        return false;
    }

    @Override
    public void forgetPendingValue() {

    }

    @Override
    public void requestSetDefault() {

    }

    @Override
    public boolean isPendingValueDefault() {
        return false;
    }

    @Override
    public boolean canResetToDefault() {
        return false;
    }

    @Override
    public void addListener(BiConsumer<Option<T>, T> changedListener) {

    }

    private class EntryBinding implements Binding<T> {
        @Override
        public void setValue(T newValue) {
            value = newValue;
            ((ListOptionImpl<T>) group).callListeners();
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public T defaultValue() {
            throw new UnsupportedOperationException();
        }
    }
}
