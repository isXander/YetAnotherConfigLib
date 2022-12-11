package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ButtonOptionImpl implements ButtonOption {
    private final Text name;
    private final Text tooltip;
    private final BiConsumer<YACLScreen, ButtonOption> action;
    private boolean available;
    private final Controller<BiConsumer<YACLScreen, ButtonOption>> controller;
    private final Binding<BiConsumer<YACLScreen, ButtonOption>> binding;

    public ButtonOptionImpl(
            @NotNull Text name,
            @Nullable Text tooltip,
            @NotNull BiConsumer<YACLScreen, ButtonOption> action,
            boolean available,
            @NotNull Function<ButtonOption, Controller<BiConsumer<YACLScreen, ButtonOption>>> controlGetter
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
    public BiConsumer<YACLScreen, ButtonOption> action() {
        return action;
    }

    @Override
    public boolean available() {
        return available;
    }

    @Override
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public @NotNull Controller<BiConsumer<YACLScreen, ButtonOption>> controller() {
        return controller;
    }

    @Override
    public @NotNull Binding<BiConsumer<YACLScreen, ButtonOption>> binding() {
        return binding;
    }

    @Override
    public @NotNull Class<BiConsumer<YACLScreen, ButtonOption>> typeClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ImmutableSet<OptionFlag> flags() {
        return ImmutableSet.of();
    }

    @Override
    public boolean changed() {
        return false;
    }

    @Override
    public @NotNull BiConsumer<YACLScreen, ButtonOption> pendingValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void requestSet(BiConsumer<YACLScreen, ButtonOption> value) {
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

    @Override
    public boolean isPendingValueDefault() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(BiConsumer<Option<BiConsumer<YACLScreen, ButtonOption>>, BiConsumer<YACLScreen, ButtonOption>> changedListener) {

    }

    private static class EmptyBinderImpl implements Binding<BiConsumer<YACLScreen, ButtonOption>> {
        @Override
        public void setValue(BiConsumer<YACLScreen, ButtonOption> value) {

        }

        @Override
        public BiConsumer<YACLScreen, ButtonOption> getValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public BiConsumer<YACLScreen, ButtonOption> defaultValue() {
            throw new UnsupportedOperationException();
        }
    }
}
