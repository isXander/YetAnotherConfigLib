package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.Binding;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NotNullBinding<T> implements Binding<T> {
    private final Binding<T> binding;

    public NotNullBinding(Binding<T> binding) {
        this.binding = binding;
    }

    @Override
    public @NotNull T getValue() {
        return Validate.notNull(binding.getValue(), "Binding's value must not be null, please use Optionals if you want null behaviour.");
    }

    @Override
    public void setValue(@NotNull T value) {
        Validate.notNull(value, "Binding's value must not be set to null, please use Optionals if you want null behaviour.");
        binding.setValue(value);
    }

    @Override
    public @NotNull T defaultValue() {
        return Validate.notNull(binding.defaultValue(), "Binding's default value must not be null, please use Optionals if you want null behaviour.");
    }
}
