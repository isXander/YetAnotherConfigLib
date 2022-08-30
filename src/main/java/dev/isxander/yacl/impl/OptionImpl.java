package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.Control;
import dev.isxander.yacl.api.Option;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class OptionImpl<T> implements Option<T> {
    private final Text name;
    private final Text tooltip;
    private final Control<T> control;
    private final Binding<T> binding;

    private @Nullable T changedValue = null;

    public OptionImpl(@NotNull Text name,
                      @Nullable Text tooltip,
                      @NotNull Control<T> control,
                      @NotNull Binding<T> binding) {
        this.name = name;
        this.tooltip = tooltip;
        this.control = control;
        this.binding = binding;
    }

    @Override
    public @NotNull Text name() {
        return name;
    }

    @Override
    public @Nullable Text tooltip() {
        return tooltip;
    }

    @Override
    public @NotNull Control<T> control() {
        return control;
    }

    @Override
    public @NotNull Binding<T> binding() {
        return binding;
    }

    @Override
    public boolean changed() {
        return !binding().getValue().equals(changedValue);
    }

    @Override
    public void requestSet(T value) {
        this.changedValue = value;
    }

    @Override
    public void applyValue() {
        if (changedValue != null)
            binding().setValue(changedValue);
    }
}
