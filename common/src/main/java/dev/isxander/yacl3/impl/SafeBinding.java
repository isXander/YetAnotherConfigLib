package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.Binding;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SafeBinding<T> implements Binding<T> {
    private final Binding<T> binding;

    public SafeBinding(Binding<T> binding) {
        this.binding = binding;
    }

    @Override
    public @NotNull T getValue() {
        return Objects.requireNonNull(binding.getValue());
    }

    @Override
    public void setValue(@NotNull T value) {
        binding.setValue(Objects.requireNonNull(value));
    }

    @Override
    public @NotNull T defaultValue() {
        return Objects.requireNonNull(binding.defaultValue());
    }
}
