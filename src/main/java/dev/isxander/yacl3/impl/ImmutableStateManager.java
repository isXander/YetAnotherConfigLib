package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.StateManager;

public class ImmutableStateManager<T> implements StateManager<T> {
    private final T value;

    public ImmutableStateManager(T value) {
        this.value = value;
    }

    @Override
    public void set(T value) {
        throw new UnsupportedOperationException("Cannot set value of immutable state manager");
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void apply() {
        // no-op
    }

    @Override
    public void resetToDefault(ResetAction action) {
        // always default
    }

    @Override
    public void sync() {
        // always synced
    }

    @Override
    public boolean isSynced() {
        return true;
    }

    @Override
    public boolean isAlwaysSynced() {
        return true;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public void addListener(StateListener<T> stateListener) {
        // as the values never change, listeners are not needed and would never be called
    }
}
