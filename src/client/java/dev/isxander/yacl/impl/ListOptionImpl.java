package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl.api.*;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListOptionImpl<T> implements ListOption<T> {
    private final Text name;
    private final Text tooltip;
    private final Binding<List<T>> binding;
    private final T initialValue;
    private final List<ListOptionEntry<T>> entries;
    private final boolean collapsed;
    private boolean available;
    private final Class<T> typeClass;
    private final ImmutableSet<OptionFlag> flags;
    private final EntryFactory entryFactory;
    private final List<BiConsumer<Option<List<T>>, List<T>>> listeners;
    private final List<BiConsumer<Option<List<T>>, List<T>>> refreshListeners;

    public ListOptionImpl(@NotNull Text name, @NotNull Text tooltip, @NotNull Binding<List<T>> binding, @NotNull T initialValue, @NotNull Class<T> typeClass, @NotNull Function<ListOptionEntry<T>, Controller<T>> controllerFunction, ImmutableSet<OptionFlag> flags, boolean collapsed, boolean available) {
        this.name = name;
        this.tooltip = tooltip;
        this.binding = binding;
        this.initialValue = initialValue;
        this.entryFactory = new EntryFactory(controllerFunction);
        this.entries = createEntries(binding().getValue());
        this.collapsed = collapsed;
        this.typeClass = typeClass;
        this.flags = flags;
        this.available = available;
        this.listeners = new ArrayList<>();
        this.refreshListeners = new ArrayList<>();
        callListeners();
    }

    @Override
    public @NotNull Text name() {
        return this.name;
    }

    @Override
    public @NotNull Text tooltip() {
        return this.tooltip;
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
    public @NotNull Class<List<T>> typeClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Class<T> elementTypeClass() {
        return typeClass;
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
    public ImmutableList<T> pendingValue() {
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
        entries.remove(entry);
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
        listeners.forEach(listener -> listener.accept(this, value));
    }

    private void onRefresh() {
        refreshListeners.forEach(listener -> listener.accept(this, pendingValue()));
        callListeners();
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
    public void addListener(BiConsumer<Option<List<T>>, List<T>> changedListener) {
        this.listeners.add(changedListener);
    }

    @Override
    public void addRefreshListener(BiConsumer<Option<List<T>>, List<T>> changedListener) {
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

    private class EntryFactory {
        private final Function<ListOptionEntry<T>, Controller<T>> controllerFunction;

        private EntryFactory(Function<ListOptionEntry<T>, Controller<T>> controllerFunction) {
            this.controllerFunction = controllerFunction;
        }

        public ListOptionEntry<T> create(T initialValue) {
            return new ListOptionEntryImpl<>(ListOptionImpl.this, initialValue, controllerFunction);
        }
    }
}
