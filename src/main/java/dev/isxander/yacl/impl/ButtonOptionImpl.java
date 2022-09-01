package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.ButtonOption;
import dev.isxander.yacl.api.Control;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@ApiStatus.Internal
public class ButtonOptionImpl implements ButtonOption {
    private final Text name;
    private final Text tooltip;
    private final Runnable action;
    private final Control<Runnable> control;
    private final Binding<Runnable> binding;

    public ButtonOptionImpl(
            @NotNull Text name,
            @Nullable Text tooltip,
            @NotNull Runnable action,
            @NotNull Function<ButtonOption, Control<Runnable>> controlGetter
    ) {
        this.name = name;
        this.tooltip = tooltip;
        this.action = action;
        this.control = controlGetter.apply(this);
        this.binding = new EmptyBinderImpl();
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
    public Runnable action() {
        return action;
    }

    @Override
    public @NotNull Control<Runnable> control() {
        return control;
    }

    @Override
    public @NotNull Binding<Runnable> binding() {
        return binding;
    }

    @Override
    public boolean changed() {
        return false;
    }

    @Override
    public Runnable pendingValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void requestSet(Runnable value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyValue() {

    }

    @Override
    public void forgetPendingValue() {

    }

    @Override
    public void requestSetDefault() {

    }

    private static class EmptyBinderImpl implements Binding<Runnable> {
        @Override
        public void setValue(Runnable value) {

        }

        @Override
        public Runnable getValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Runnable defaultValue() {
            throw new UnsupportedOperationException();
        }
    }
}
