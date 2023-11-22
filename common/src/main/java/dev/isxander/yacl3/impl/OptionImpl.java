package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class OptionImpl<T> implements Option<T> {
    private final Component name;
    private OptionDescription description;
    private final Controller<T> controller;
    private final Binding<T> binding;
    private boolean available;

    private final ImmutableSet<OptionFlag> flags;

    private T pendingValue;

    private final List<BiConsumer<Option<T>, T>> listeners;
    private int listenerTriggerDepth = 0;

    public OptionImpl(
            @NotNull Component name,
            @NotNull Function<T, OptionDescription> descriptionFunction,
            @NotNull Function<Option<T>, Controller<T>> controlGetter,
            @NotNull Binding<T> binding,
            boolean available,
            ImmutableSet<OptionFlag> flags,
            @NotNull Collection<BiConsumer<Option<T>, T>> listeners
    ) {
        this.name = name;
        this.binding = new SafeBinding<>(binding);
        this.available = available;
        this.flags = flags;
        this.listeners = new ArrayList<>(listeners);

        this.pendingValue = binding.getValue();
        this.controller = controlGetter.apply(this);

        addListener((opt, pending) -> description = descriptionFunction.apply(pending));
        triggerListeners(true);
    }

    @Override
    public @NotNull Component name() {
        return name;
    }

    @Override
    public @NotNull OptionDescription description() {
        return this.description;
    }

    @Override
    public @NotNull Component tooltip() {
        return description.text();
    }

    @Override
    public @NotNull Controller<T> controller() {
        return controller;
    }

    @Override
    public @NotNull Binding<T> binding() {
        return binding;
    }

    @Override
    public boolean available() {
        return available;
    }

    @Override
    public void setAvailable(boolean available) {
        boolean changed = this.available != available;

        this.available = available;

        if (changed) {
            if (!available) {
                this.pendingValue = binding().getValue();
            }
            this.triggerListeners(!available);
        }
    }

    @Override
    public @NotNull ImmutableSet<OptionFlag> flags() {
        return flags;
    }

    @Override
    public boolean changed() {
        return !binding().getValue().equals(pendingValue);
    }

    @Override
    public @NotNull T pendingValue() {
        return pendingValue;
    }

    @Override
    public void requestSet(@NotNull T value) {
        Validate.notNull(value, "`value` cannot be null");

        pendingValue = value;
        this.triggerListeners(true);
    }

    @Override
    public boolean applyValue() {
        if (changed()) {
            binding().setValue(pendingValue);
            return true;
        }
        return false;
    }

    @Override
    public void forgetPendingValue() {
        requestSet(binding().getValue());
    }

    @Override
    public void requestSetDefault() {
        requestSet(binding().defaultValue());
    }

    @Override
    public boolean isPendingValueDefault() {
        return binding().defaultValue().equals(pendingValue());
    }

    @Override
    public void addListener(BiConsumer<Option<T>, T> changedListener) {
        this.listeners.add(changedListener);
    }

    private void triggerListeners(boolean bypass) {
        if (bypass || listenerTriggerDepth == 0) {
            if (listenerTriggerDepth > 10) {
                throw new IllegalStateException("Listener trigger depth exceeded 10! This means a listener triggered a listener etc etc 10 times deep. This is likely a bug in the mod using YACL!");
            }

            this.listenerTriggerDepth++;

            for (BiConsumer<Option<T>, T> listener : listeners) {
                try {
                    listener.accept(this, pendingValue);
                } catch (Exception e) {
                    YACLConstants.LOGGER.error("Exception whilst triggering listener for option '%s'".formatted(name.getString()), e);
                }
            }

            this.listenerTriggerDepth--;
        }
    }

    @ApiStatus.Internal
    public static class BuilderImpl<T> implements Builder<T> {
        private Component name = Component.literal("Name not specified!").withStyle(ChatFormatting.RED);

        private Function<T, OptionDescription> descriptionFunction = pending -> OptionDescription.EMPTY;

        private Function<Option<T>, Controller<T>> controlGetter;

        private Binding<T> binding;

        private boolean available = true;

        private boolean instant = false;

        private final Set<OptionFlag> flags = new HashSet<>();

        private final List<BiConsumer<Option<T>, T>> listeners = new ArrayList<>();

        @Override
        public Builder<T> name(@NotNull Component name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        @Override
        public Builder<T> description(@NotNull OptionDescription description) {
            return description(opt -> description);
        }

        @Override
        public Builder<T> description(@NotNull Function<T, OptionDescription> descriptionFunction) {
            this.descriptionFunction = descriptionFunction;
            return this;
        }

        @Override
        public Builder<T> controller(@NotNull Function<Option<T>, ControllerBuilder<T>> controllerBuilder) {
            Validate.notNull(controllerBuilder, "`controllerBuilder` cannot be null");

            return customController(opt -> controllerBuilder.apply(opt).build());
        }

        @Override
        public Builder<T> customController(@NotNull Function<Option<T>, Controller<T>> control) {
            Validate.notNull(control, "`control` cannot be null");

            this.controlGetter = control;
            return this;
        }

        @Override
        public Builder<T> binding(@NotNull Binding<T> binding) {
            Validate.notNull(binding, "`binding` cannot be null");

            this.binding = binding;
            return this;
        }

        @Override
        public Builder<T> binding(@NotNull T def, @NotNull Supplier<@NotNull T> getter, @NotNull Consumer<@NotNull T> setter) {
            Validate.notNull(def, "`def` must not be null");
            Validate.notNull(getter, "`getter` must not be null");
            Validate.notNull(setter, "`setter` must not be null");

            this.binding = Binding.generic(def, getter, setter);
            return this;
        }

        @Override
        public Builder<T> available(boolean available) {
            this.available = available;
            return this;
        }

        @Override
        public Builder<T> flag(@NotNull OptionFlag... flag) {
            Validate.notNull(flag, "`flag` must not be null");

            this.flags.addAll(Arrays.asList(flag));
            return this;
        }

        @Override
        public Builder<T> flags(@NotNull Collection<? extends OptionFlag> flags) {
            Validate.notNull(flags, "`flags` must not be null");

            this.flags.addAll(flags);
            return this;
        }

        @Override
        public Builder<T> instant(boolean instant) {
            this.instant = instant;
            return this;
        }

        @Override
        public Builder<T> listener(@NotNull BiConsumer<Option<T>, T> listener) {
            this.listeners.add(listener);
            return this;
        }

        @Override
        public Builder<T> listeners(@NotNull Collection<BiConsumer<Option<T>, T>> listeners) {
            this.listeners.addAll(listeners);
            return this;
        }

        @Override
        public Option<T> build() {
            Validate.notNull(controlGetter, "`control` must not be null when building `Option`");
            Validate.notNull(binding, "`binding` must not be null when building `Option`");
            Validate.isTrue(!instant || flags.isEmpty(), "instant application does not support option flags");

            if (instant) {
                listeners.add((opt, pendingValue) -> opt.applyValue());
            }

            return new OptionImpl<>(name, descriptionFunction, controlGetter, binding, available, ImmutableSet.copyOf(flags), listeners);
        }
    }
}
