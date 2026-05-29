package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.StateManager;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class XMappedStateManager<I, O> implements StateManager<O> {
    private final StateManager<I> backing;
    private final Function<I, O> inputToOutput;
    private final Function<O, @Nullable I> outputToInput;

    public XMappedStateManager(
            StateManager<I> backing,
            Function<I, O> inputToOutput,
            Function<O, @Nullable I> outputToInput
    ) {
        this.backing = backing;
        this.inputToOutput = inputToOutput;
        this.outputToInput = outputToInput;
    }


    @Override
    public void set(O value) {
        var input = this.outputToInput.apply(value);
        if (input != null) this.backing.set(input);
    }

    @Override
    public O get() {
        return this.inputToOutput.apply(this.backing.get());
    }

    @Override
    public void apply() {
        this.backing.apply();
    }

    @Override
    public void resetToDefault(ResetAction action) {
        this.backing.resetToDefault(action);
    }

    @Override
    public void sync() {
        this.backing.sync();
    }

    @Override
    public boolean isSynced() {
        return this.backing.isSynced();
    }

    @Override
    public boolean isAlwaysSynced() {
        return this.backing.isAlwaysSynced();
    }

    @Override
    public boolean isDefault() {
        return this.backing.isDefault();
    }

    @Override
    public void addListener(StateListener<O> stateListener) {
        this.backing.addListener((oldValue, newValue) ->
                stateListener.onStateChange(inputToOutput.apply(oldValue), inputToOutput.apply(newValue)));
    }
}
