package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ActionController;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ApiStatus.Internal
public final class ButtonOptionImpl implements ButtonOption {
    private final Component name;
    private final OptionDescription description;
    private final BiConsumer<YACLScreen, ButtonOption> action;
    private boolean available;
    private final Controller<BiConsumer<YACLScreen, ButtonOption>> controller;
    private final Binding<BiConsumer<YACLScreen, ButtonOption>> binding;

    public ButtonOptionImpl(
            @NotNull Component name,
            @Nullable OptionDescription description,
            @NotNull BiConsumer<YACLScreen, ButtonOption> action,
            @Nullable Component text,
            boolean available
    ) {
        this.name = name;
        this.description = description;
        this.action = action;
        this.available = available;
        this.controller = text != null ? new ActionController(this, text) : new ActionController(this);
        this.binding = new EmptyBinderImpl();
    }

    @Override
    public @NotNull Component name() {
        return name;
    }

    @Override
    public @NotNull OptionDescription description() {
        return description;
    }

    @Override
    public @NotNull Component tooltip() {
        return description().text();
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

    @ApiStatus.Internal
    public static final class BuilderImpl implements Builder {
        private Component name;
        private Component text = null;
        private OptionDescription description = OptionDescription.EMPTY;
        private boolean available = true;
        private BiConsumer<YACLScreen, ButtonOption> action;

        @Override
        public Builder name(@NotNull Component name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        @Override
        public Builder text(@NotNull Component text) {
            Validate.notNull(text, "`text` cannot be null");

            this.text = text;
            return this;
        }

        @Override
        public Builder description(@NotNull OptionDescription description) {
            Validate.notNull(description, "`description` cannot be null");

            this.description = description;
            return this;
        }

        @Override
        public Builder action(@NotNull BiConsumer<YACLScreen, ButtonOption> action) {
            Validate.notNull(action, "`action` cannot be null");

            this.action = action;
            return this;
        }

        @Override
        @Deprecated
        public Builder action(@NotNull Consumer<YACLScreen> action) {
            Validate.notNull(action, "`action` cannot be null");

            this.action = (screen, button) -> action.accept(screen);
            return this;
        }

        @Override
        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        @Override
        public ButtonOption build() {
            Validate.notNull(name, "`name` must not be null when building `ButtonOption`");
            Validate.notNull(action, "`action` must not be null when building `ButtonOption`");

            return new ButtonOptionImpl(name, description, action, text, available);
        }
    }
}
