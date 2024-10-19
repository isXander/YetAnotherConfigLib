package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.StateManager;

public class InstantStateManager<T> implements StateManager<T>, ProvidesBindingForDeprecation<T> {
    private final Binding<T> binding;
    private StateListener<T> stateListener;

    public InstantStateManager(Binding<T> binding) {
        this.binding = binding;
        this.stateListener = StateListener.noop();
    }

    @Override
    public void set(T value) {
        boolean changed = !this.get().equals(value);

        this.binding.setValue(value);

        if (changed) stateListener.onStateChange(this.get(), value);
    }

    @Override
    public T get() {
        return this.binding.getValue();
    }

    @Override
    public void apply() {
        // no-op, state is always applied
    }

    @Override
    public void resetToDefault(ResetAction action) {
        this.set(binding.defaultValue());
    }

    @Override
    public void sync() {
        // no-op, state is always synced
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
        return binding.defaultValue().equals(this.get());
    }

    @Override
    public void addListener(StateListener<T> stateListener) {
        this.stateListener = this.stateListener.andThen(stateListener);
    }

    @Override
    public Binding<T> getBinding() {
        return binding;
    }
}
