package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.Control;
import dev.isxander.yacl.api.Option;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@ApiStatus.Internal
public class OptionImpl<T> implements Option<T> {
    private final Text name;
    private final Text tooltip;
    private final Control<T> control;
    private final Binding<T> binding;

    private T pendingValue;

    public OptionImpl(
            @NotNull Text name,
            @Nullable Text tooltip,
            @NotNull Function<Option<T>, Control<T>> controlGetter,
            @NotNull Binding<T> binding
    ) {
        this.name = name;
        this.tooltip = tooltip;
        this.control = controlGetter.apply(this);
        this.binding = binding;
        this.pendingValue = binding().getValue();
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
        return !binding().getValue().equals(pendingValue);
    }

    @Override
    public T pendingValue() {
        return pendingValue;
    }

    @Override
    public void requestSet(T value) {
        pendingValue = value;
    }

    @Override
    public void applyValue() {
        binding().setValue(pendingValue);
    }

    @Override
    public void forgetPendingValue() {
        pendingValue = binding().getValue();
    }

    @Override
    public void requestSetDefault() {
        pendingValue = binding().defaultValue();
    }
}
