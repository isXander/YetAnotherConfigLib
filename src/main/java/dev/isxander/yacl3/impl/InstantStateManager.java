package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.StateManager;

public class InstantStateManager<T> implements StateManager<T>, ProvidesBindingForDeprecation<T> {

	private final Binding<T> binding;
	private final T previousValue;
	private T pendingValue;
	private StateListener<T> stateListener;
	private boolean requiresSave = false;

    public InstantStateManager(Binding<T> binding) {
	    this.binding = binding;
	    this.previousValue = binding.getValue();
	    this.pendingValue = binding.getValue();
	    this.stateListener = StateListener.noop();
    }

    @Override
    public void set(T value) {
	    boolean changed = !this.pendingValue.equals(value);
	    boolean previousValue = this.previousValue.equals(value);

	    this.binding.setValue(value);
	    this.pendingValue = value;

	    if (previousValue) {
		    this.requiresSave = false;
	    }
	    if (changed && !previousValue) {
		    this.requiresSave = true;
	    }
	    if (changed) {
		    this.stateListener.onStateChange(this.pendingValue, value);
	    }
    }

    @Override
    public T get() {
	    return this.pendingValue;
    }

    @Override
    public void apply() {
	    this.requiresSave = false;
    }

    @Override
    public void resetToDefault(ResetAction action) {
	    this.set(this.binding.defaultValue());
    }

    @Override
    public void sync() {
	    this.set(this.previousValue);
    }

    @Override
    public boolean isSynced() {
	    return !this.requiresSave;
    }

    @Override
    public boolean isDefault() {
	    return this.binding.defaultValue().equals(this.pendingValue);
    }

    @Override
    public void addListener(StateListener<T> stateListener) {
        this.stateListener = this.stateListener.andThen(stateListener);
    }

    @Override
    public Binding<T> getBinding() {
        return this.binding;
    }
}
