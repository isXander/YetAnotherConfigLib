package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.impl.utils.YACLConstants;
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
    private final StateManager<List<T>> stateManager;
    private final Supplier<T> initialValue;
    private final List<ListOptionEntry<T>> entries;
    private final boolean collapsed;
    private boolean available;
    private final int minimumNumberOfEntries;
    private final int maximumNumberOfEntries;
    private final boolean insertEntriesAtEnd;
    private final ImmutableSet<OptionFlag> flags;
    private final EntryFactory entryFactory;

    private final List<OptionEventListener<List<T>>> listeners;
    private final List<Runnable> refreshListeners;
    private int currentListenerDepth = 0;

    public ListOptionImpl(@NotNull Component name, @NotNull OptionDescription description, @NotNull StateManager<List<T>> stateManager, @NotNull Supplier<T> initialValue, @NotNull Function<ListOptionEntry<T>, Controller<T>> controllerFunction, ImmutableSet<OptionFlag> flags, boolean collapsed, boolean available, int minimumNumberOfEntries, int maximumNumberOfEntries, boolean insertEntriesAtEnd, Collection<OptionEventListener<List<T>>> listeners) {
        this.name = name;
        this.description = description;
        this.stateManager = stateManager;
        this.initialValue = initialValue;
        this.entryFactory = new EntryFactory(controllerFunction);
        this.entries = createEntries(binding().getValue());
        this.collapsed = collapsed;
        this.flags = flags;
        this.available = available;
        this.minimumNumberOfEntries = minimumNumberOfEntries;
        this.maximumNumberOfEntries = maximumNumberOfEntries;
        this.insertEntriesAtEnd = insertEntriesAtEnd;
        this.listeners = new ArrayList<>();
        this.listeners.addAll(listeners);
        this.refreshListeners = new ArrayList<>();

        this.stateManager.addListener((oldValue, newValue) ->
                triggerListener(OptionEventListener.Event.STATE_CHANGE, false));
        triggerListener(OptionEventListener.Event.INITIAL, false);
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
    public @NotNull StateManager<List<T>> stateManager() {
        return stateManager;
    }

    @Override
    @Deprecated
    public @NotNull Binding<List<T>> binding() {
        if (stateManager instanceof ProvidesBindingForDeprecation) {
            return ((ProvidesBindingForDeprecation<List<T>>) stateManager).getBinding();
        }
        throw new UnsupportedOperationException("Binding is not available for this option - using a new state manager which does not directly expose the binding as it may not have one.");
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
    public ListOptionEntry<T> insertNewEntry() {
        ListOptionEntry<T> newEntry = entryFactory.create(initialValue.get());
        if (insertEntriesAtEnd) {
            entries.add(newEntry);
        } else {
            // insert at top
            entries.add(0, newEntry);
        }
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
    public void requestSet(@NotNull List<T> value) {
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
        boolean changed = this.available != available;

        this.available = available;

        if (changed) {
            if (!available) {
                this.stateManager.sync();
            }
            this.triggerListener(OptionEventListener.Event.AVAILABILITY_CHANGE, !available);
        }
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
    public void addEventListener(OptionEventListener<List<T>> listener) {
        this.listeners.add(listener);
    }

    @Override
    @Deprecated
    public void addListener(BiConsumer<Option<List<T>>, List<T>> changedListener) {
        addEventListener((opt, event) -> changedListener.accept(opt, opt.pendingValue()));
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

    void triggerListener(OptionEventListener.Event event, boolean allowDepth) {
        if (allowDepth || currentListenerDepth == 0) {
            Validate.isTrue(
                    currentListenerDepth <= 10,
                    "Listener depth exceeded 10! Possible cyclic listener pattern: a listener triggered an event that triggered the initial event etc etc."
            );

            currentListenerDepth++;

            for (OptionEventListener<List<T>> listener : listeners) {
                listener.onEvent(this, event);
            }

            currentListenerDepth--;
        }
    }

    private void onRefresh() {
        refreshListeners.forEach(Runnable::run);
        triggerListener(OptionEventListener.Event.OTHER, true);
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
        private final Set<OptionFlag> flags = new HashSet<>();
        private Supplier<T> initialValue;
        private boolean collapsed = false;
        private boolean available = true;
        private int minimumNumberOfEntries = 0;
        private int maximumNumberOfEntries = Integer.MAX_VALUE;
        private boolean insertEntriesAtEnd = false;
        private final List<OptionEventListener<List<T>>> listeners = new ArrayList<>();

        private Binding<List<T>> binding;
        private StateManager<List<T>> stateManager;

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
        public Builder<T> initial(@NotNull Supplier<T> initialValue) {
            Validate.notNull(initialValue, "`initialValue` cannot be empty");

            this.initialValue = initialValue;
            return this;
        }

        @Override
        public Builder<T> initial(@NotNull T initialValue) {
            Validate.notNull(initialValue, "`initialValue` cannot be empty");

            this.initialValue = () -> initialValue;
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
        public Builder<T> state(@NotNull StateManager<List<T>> stateManager) {
            Validate.notNull(stateManager, "`stateManager` cannot be null");
            Validate.isTrue(binding == null, "Cannot set state manager if binding is already set");

            this.stateManager = stateManager;
            return this;
        }

        @Override
        public Builder<T> binding(@NotNull Binding<List<T>> binding) {
            Validate.notNull(binding, "`binding` cannot be null");
            Validate.isTrue(stateManager == null, "Cannot set binding if state manager is already set");

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
        public Builder<T> insertEntriesAtEnd(boolean insertAtEnd) {
            this.insertEntriesAtEnd = insertAtEnd;
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
        public Builder<T> addListener(@NotNull OptionEventListener<List<T>> listener) {
            Validate.notNull(listener, "`listener` must not be null");

            this.listeners.add(listener);
            return this;
        }

        @Override
        public Builder<T> addListeners(@NotNull Collection<@NotNull OptionEventListener<List<T>>> optionEventListeners) {
            Validate.notNull(optionEventListeners, "`optionEventListeners` must not be null");

            this.listeners.addAll(optionEventListeners);
            return this;
        }

        @Override
        public Builder<T> listener(@NotNull BiConsumer<Option<List<T>>, List<T>> listener) {
            Validate.notNull(listener, "`listener` must not be null");

            return this.addListener((opt, event) -> listener.accept(opt, opt.pendingValue()));
        }

        @Override
        public Builder<T> listeners(@NotNull Collection<BiConsumer<Option<List<T>>, List<T>>> listeners) {
            Validate.notNull(listeners, "`listeners` must not be null");

            this.addListeners(listeners.stream()
                    .map(listener ->
                            (OptionEventListener<List<T>>) (opt, event) ->
                                    listener.accept(opt, opt.pendingValue())
                    ).toList()
            );
            return this;
        }

        @Override
        public ListOption<T> build() {
            Validate.notNull(controllerFunction, "`controller` must not be null");
            Validate.notNull(initialValue, "`initialValue` must not be null");
            Validate.isTrue(stateManager != null || binding != null, "Either a state manager or binding must be set");

            if (stateManager == null) {
                stateManager = StateManager.createSimple(binding);
            }

            return new ListOptionImpl<>(name, description, stateManager, initialValue, controllerFunction, ImmutableSet.copyOf(flags), collapsed, available, minimumNumberOfEntries, maximumNumberOfEntries, insertEntriesAtEnd, listeners);
        }
    }
}
