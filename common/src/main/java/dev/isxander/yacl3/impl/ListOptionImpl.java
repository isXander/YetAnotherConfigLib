package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class ListOptionImpl<T> implements ListOption<T> {
    private final Component name;
    private final OptionDescription description;
    private final Binding<List<T>> binding;
    private final T initialValue;
    private final List<ListOptionEntry<T>> entries;
    private final boolean collapsed;
    private boolean available;
    private final int minimumNumberOfEntries;
    private final int maximumNumberOfEntries;
    private final ImmutableSet<OptionFlag> flags;
    private final EntryFactory entryFactory;
    private final List<BiConsumer<Option<List<T>>, List<T>>> listeners;
    private final List<Runnable> refreshListeners;

    public ListOptionImpl(@NotNull Component name, @NotNull OptionDescription description, @NotNull Binding<List<T>> binding, @NotNull T initialValue, @NotNull Function<ListOptionEntry<T>, Controller<T>> controllerFunction, ImmutableSet<OptionFlag> flags, boolean collapsed, boolean available, int minimumNumberOfEntries, int maximumNumberOfEntries, Collection<BiConsumer<Option<List<T>>, List<T>>> listeners) {
        this.name = name;
        this.description = description;
        this.binding = binding;
        this.initialValue = initialValue;
        this.entryFactory = new EntryFactory(controllerFunction);
        this.entries = createEntries(binding().getValue());
        this.collapsed = collapsed;
        this.flags = flags;
        this.available = available;
        this.minimumNumberOfEntries = minimumNumberOfEntries;
        this.maximumNumberOfEntries = maximumNumberOfEntries;
        this.listeners = new ArrayList<>();
        this.listeners.addAll(listeners);
        this.refreshListeners = new ArrayList<>();
        callListeners();
    }

    @Override
    public @NotNull Component name() {
        return this.name;
    }

    @Override
    public @NotNull OptionDescription description() {
        return this.description;
    }

    @Override
    public @NotNull Component tooltip() {
        return description().text();
    }

    @Override
    public @NotNull ImmutableList<ListOptionEntry<T>> options() {
        return ImmutableList.copyOf(entries);
    }

    @Override
    public @NotNull Controller<List<T>> controller() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Binding<List<T>> binding() {
        return binding;
    }

    @Override
    public boolean collapsed() {
        return collapsed;
    }

    @Override
    public @NotNull ImmutableSet<OptionFlag> flags() {
        return flags;
    }

    @Override
    public @NotNull ImmutableList<T> pendingValue() {
        return ImmutableList.copyOf(entries.stream().map(Option::pendingValue).toList());
    }

    @Override
    public void insertEntry(int index, ListOptionEntry<?> entry) {
        entries.add(index, (ListOptionEntry<T>) entry);
        onRefresh();
    }

    @Override
    public ListOptionEntry<T> insertNewEntryToTop() {
        ListOptionEntry<T> newEntry = entryFactory.create(initialValue);
        entries.add(0, newEntry);
        onRefresh();
        return newEntry;
    }

    @Override
    public void removeEntry(ListOptionEntry<?> entry) {
        if (entries.remove(entry))
            onRefresh();
    }

    @Override
    public int indexOf(ListOptionEntry<?> entry) {
        return entries.indexOf(entry);
    }

    @Override
    public void requestSet(List<T> value) {
        entries.clear();
        entries.addAll(createEntries(value));
        onRefresh();
    }

    @Override
    public boolean changed() {
        return !binding().getValue().equals(pendingValue());
    }

    @Override
    public boolean applyValue() {
        if (changed()) {
            binding().setValue(pendingValue());
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
    public boolean available() {
        return available;
    }

    @Override
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public int numberOfEntries() {
        return this.entries.size();
    }
    @Override
    public int maximumNumberOfEntries() {
        return this.maximumNumberOfEntries;
    }
    @Override
    public int minimumNumberOfEntries() {
        return this.minimumNumberOfEntries;
    }

    @Override
    public void addListener(BiConsumer<Option<List<T>>, List<T>> changedListener) {
        this.listeners.add(changedListener);
    }

    @Override
    public void addRefreshListener(Runnable changedListener) {
        this.refreshListeners.add(changedListener);
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    private List<ListOptionEntry<T>> createEntries(Collection<T> values) {
        return values.stream().map(entryFactory::create).collect(Collectors.toList());
    }

    void callListeners() {
        List<T> pendingValue = pendingValue();
        this.listeners.forEach(listener -> listener.accept(this, pendingValue));
    }

    private void onRefresh() {
        refreshListeners.forEach(Runnable::run);
        callListeners();
    }

    private class EntryFactory {
        private final Function<ListOptionEntry<T>, Controller<T>> controllerFunction;

        private EntryFactory(Function<ListOptionEntry<T>, Controller<T>> controllerFunction) {
            this.controllerFunction = controllerFunction;
        }

        public ListOptionEntry<T> create(T initialValue) {
            return new ListOptionEntryImpl<>(ListOptionImpl.this, initialValue, controllerFunction);
        }
    }

    @ApiStatus.Internal
    public static final class BuilderImpl<T> implements Builder<T> {
        private Component name = Component.empty();
        private OptionDescription description = OptionDescription.EMPTY;
        private Function<ListOptionEntry<T>, Controller<T>> controllerFunction;
        private Binding<List<T>> binding = null;
        private final Set<OptionFlag> flags = new HashSet<>();
        private T initialValue;
        private boolean collapsed = false;
        private boolean available = true;
        private int minimumNumberOfEntries = 0;
        private int maximumNumberOfEntries = Integer.MAX_VALUE;
        private final List<BiConsumer<Option<List<T>>, List<T>>> listeners = new ArrayList<>();

        @Override
        public Builder<T> name(@NotNull Component name) {
            Validate.notNull(name, "`name` must not be null");

            this.name = name;
            return this;
        }

        @Override
        public Builder<T> description(@NotNull OptionDescription description) {
            Validate.notNull(description, "`description` must not be null");

            this.description = description;
            return this;
        }

        @Override
        public Builder<T> initial(@NotNull T initialValue) {
            Validate.notNull(initialValue, "`initialValue` cannot be empty");

            this.initialValue = initialValue;
            return this;
        }

        @Override
        public Builder<T> controller(@NotNull Function<Option<T>, ControllerBuilder<T>> controller) {
            Validate.notNull(controller, "`controller` cannot be null");

            this.controllerFunction = opt -> controller.apply(opt).build();
            return this;
        }

        @Override
        public Builder<T> customController(@NotNull Function<ListOptionEntry<T>, Controller<T>> control) {
            Validate.notNull(control, "`control` cannot be null");

            this.controllerFunction = control;
            return this;
        }

        @Override
        public Builder<T> binding(@NotNull Binding<List<T>> binding) {
            Validate.notNull(binding, "`binding` cannot be null");

            this.binding = binding;
            return this;
        }

        @Override
        public Builder<T> binding(@NotNull List<T> def, @NotNull Supplier<@NotNull List<T>> getter, @NotNull Consumer<@NotNull List<T>> setter) {
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
        public Builder<T> minimumNumberOfEntries(int number) {
            this.minimumNumberOfEntries = number;
            return this;
        }

        @Override
        public Builder<T> maximumNumberOfEntries(int number) {
            this.maximumNumberOfEntries = number;
            return this;
        }

        @Override
        public Builder<T> flag(@NotNull OptionFlag... flag) {
            Validate.notNull(flag, "`flag` must not be null");

            this.flags.addAll(Arrays.asList(flag));
            return this;
        }

        @Override
        public Builder<T> flags(@NotNull Collection<OptionFlag> flags) {
            Validate.notNull(flags, "`flags` must not be null");

            this.flags.addAll(flags);
            return this;
        }

        @Override
        public Builder<T> collapsed(boolean collapsible) {
            this.collapsed = collapsible;
            return this;
        }

        @Override
        public Builder<T> listener(@NotNull BiConsumer<Option<List<T>>, List<T>> listener) {
            this.listeners.add(listener);
            return this;
        }

        @Override
        public Builder<T> listeners(@NotNull Collection<BiConsumer<Option<List<T>>, List<T>>> listeners) {
            this.listeners.addAll(listeners);
            return this;
        }

        @Override
        public ListOption<T> build() {
            Validate.notNull(controllerFunction, "`controller` must not be null");
            Validate.notNull(binding, "`binding` must not be null");
            Validate.notNull(initialValue, "`initialValue` must not be null");

            return new ListOptionImpl<>(name, description, binding, initialValue, controllerFunction, ImmutableSet.copyOf(flags), collapsed, available, minimumNumberOfEntries, maximumNumberOfEntries, listeners);
        }
    }
}
