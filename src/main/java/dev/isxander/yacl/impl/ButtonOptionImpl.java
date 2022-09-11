package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.ButtonOption;
import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Storage;
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
    private final Controller<Consumer<YACLScreen>> controller;
    private final Binding<Consumer<YACLScreen>, Void> binding;

    public ButtonOptionImpl(
            @NotNull Text name,
            @Nullable Text tooltip,
            @NotNull Consumer<YACLScreen> action,
            @NotNull Function<ButtonOption, Controller<Consumer<YACLScreen>>> controlGetter
    ) {
        this.name = name;
        this.tooltip = tooltip;
        this.action = action;
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
    public @NotNull Controller<Consumer<YACLScreen>> controller() {
        return controller;
    }

    @Override
    public @NotNull Binding<Consumer<YACLScreen>, Void> binding() {
        return binding;
    }

    @Override
    public @NotNull Storage<Void> storage() {
        return Storage.EMPTY;
    }

    @Override
    public @NotNull Class<Consumer<YACLScreen>> typeClass() {
        throw new UnsupportedOperationException();
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

    private static class EmptyBinderImpl implements Binding<Consumer<YACLScreen>, Void> {
        @Override
        public void setValue(Void storage, Consumer<YACLScreen> value) {

        }

        @Override
        public Consumer<YACLScreen> getValue(Void storage) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Consumer<YACLScreen> defaultValue(Void storage) {
            throw new UnsupportedOperationException();
        }
    }
}
