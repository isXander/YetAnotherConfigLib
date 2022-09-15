package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.ButtonOption;
import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.OptionFlag;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@ApiStatus.Internal
public class ButtonOptionImpl implements ButtonOption {
    private final Text name;
    private final Text tooltip;
    private final Consumer<YACLScreen> action;
    private final boolean available;
    private final Controller<Consumer<YACLScreen>> controller;
    private final Binding<Consumer<YACLScreen>> binding;

    public ButtonOptionImpl(
            @NotNull Text name,
            @Nullable Text tooltip,
            @NotNull Consumer<YACLScreen> action,
            boolean available,
            @NotNull Function<ButtonOption, Controller<Consumer<YACLScreen>>> controlGetter
    ) {
        this.name = name;
        this.tooltip = tooltip;
        this.action = action;
        this.available = available;
        this.controller = controlGetter.apply(this);
        this.binding = new EmptyBinderImpl();
    }

    @Override
    public @NotNull Text name() {
        return name;
    }

    @Override
    public @NotNull Text tooltip() {
        return tooltip;
    }

    @Override
    public Consumer<YACLScreen> action() {
        return action;
    }

    @Override
    public boolean available() {
        return available;
    }

    @Override
    public @NotNull Controller<Consumer<YACLScreen>> controller() {
        return controller;
    }

    @Override
    public @NotNull Binding<Consumer<YACLScreen>> binding() {
        return binding;
    }

    @Override
    public @NotNull Class<Consumer<YACLScreen>> typeClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ImmutableSet<OptionFlag> flags() {
        return ImmutableSet.of();
    }

    @Override
    public boolean requiresRestart() {
        return false;
    }

    @Override
    public boolean changed() {
        return false;
    }

    @Override
    public @NotNull Consumer<YACLScreen> pendingValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void requestSet(Consumer<YACLScreen> value) {
        throw new UnsupportedOperationException();
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

    private static class EmptyBinderImpl implements Binding<Consumer<YACLScreen>> {
        @Override
        public void setValue(Consumer<YACLScreen> value) {

        }

        @Override
        public Consumer<YACLScreen> getValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Consumer<YACLScreen> defaultValue() {
            throw new UnsupportedOperationException();
        }
    }
}
