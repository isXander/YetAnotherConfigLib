package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class MapOptionImpl<S, T> implements MapOption<S, T> {
	private final Component name;
	private final OptionDescription description;
	private final Binding<Map<S, T>> binding;
	private final Supplier<Map.Entry<S, T>> initialValue;
	private final List<MapOptionEntry<S, T>> entries;
	private final boolean collapsed;
	private boolean available;
	private final int minimumNumberOfEntries;
	private final int maximumNumberOfEntries;
	private final boolean insertEntriesAtEnd;
	private final ImmutableSet<OptionFlag> flags;
	private final EntryFactory entryFactory;

	private final List<BiConsumer<Option<Map<S, T>>, Map<S, T>>> listeners;
	private final List<Runnable> refreshListeners;
	private int listenerTriggerDepth = 0;

	@Override
	public @NotNull Controller<Map<S, T>> controller() {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Binding<Map<S, T>> binding() {
		return binding;
	}

	public MapOptionImpl(@NotNull Component name, @NotNull OptionDescription description, @NotNull Binding<Map<S, T>> binding, @NotNull Supplier<Map.Entry<S, T>> initialValue, @NotNull Function<MapOptionEntry<S, T>, Controller<S>> keyControllerFunction, @NotNull Function<MapOptionEntry<S, T>, Controller<T>> valueControllerFunction, ImmutableSet<OptionFlag> flags, boolean collapsed, boolean available, int minimumNumberOfEntries, int maximumNumberOfEntries, boolean insertEntriesAtEnd, Collection<BiConsumer<Option<Map<S, T>>, Map<S, T>>> listeners) {
		this.name = name;
		this.description = description;
		this.binding = new SafeBinding<>(binding);
		this.initialValue = initialValue;
		this.entryFactory = new EntryFactory(keyControllerFunction, valueControllerFunction);
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
		callListeners(true);
	}

	@Override
	public @NotNull ImmutableList<MapOptionEntry<S, T>> options() {
		return ImmutableList.copyOf(entries);
	}

	@Override
	public int numberOfEntries() {
		return entries.size();
	}

	@Override
	public int maximumNumberOfEntries() {
		return maximumNumberOfEntries;
	}

	@Override
	public int minimumNumberOfEntries() {
		return minimumNumberOfEntries;
	}

	@Override
	public MapOptionEntry<S, T> insertNewEntry() {
		MapOptionEntry<S, T> newEntry = entryFactory.create(initialValue.get());
		if (insertEntriesAtEnd) {
			entries.add(newEntry);
		} else {
			// insert at top
			entries.add(0, newEntry);
		}
		onRefresh();
		return newEntry;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void insertEntry(int index, MapOptionEntry<?, ?> entry) {
		entries.add(index, (MapOptionEntry<S, T>) entry);
		onRefresh();
	}

	@Override
	public int indexOf(MapOptionEntry<?, ?> entry) {
		return entries.indexOf(entry);
	}

	@Override
	public void removeEntry(MapOptionEntry<?, ?> entry) {
		if (entries.remove(entry)) {
			onRefresh();
		}
	}

	@Override
	public void addRefreshListener(Runnable changedListener) {
		this.refreshListeners.add(changedListener);
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
			callListeners(false);
		}
	}

	@Override
	public @NotNull ImmutableSet<OptionFlag> flags() {
		return flags;
	}

	@Override
	public boolean changed() {
		return !binding().getValue().equals(pendingValue());
	}

	@Override
	public @NotNull Map<S, T> pendingValue() {
		// TODO: Refactor into a method
		Map<S, T> mapEntries = new HashMap<>();

		for (MapOptionEntry<S, T> entry : entries) {
			var mapEntry = (Map.Entry<S, T>) entry.pendingValue();
			mapEntries.put(mapEntry.getKey(), mapEntry.getValue());
		}

		return mapEntries;
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
	public void addListener(BiConsumer<Option<Map<S, T>>, Map<S, T>> changedListener) {
		this.listeners.add(changedListener);
	}

	@Override
	public void requestSet(@NotNull Map<S, T> value) {
		entries.clear();
		entries.addAll(createEntries(value));
		onRefresh();
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
	public boolean collapsed() {
		return collapsed;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	private List<MapOptionEntry<S, T>> createEntries(Map<S, T> values) {
		return values.entrySet().stream().filter(Objects::nonNull).map(entryFactory::create).collect(Collectors.toList());
	}

	void callListeners(boolean bypass) {
		Map<S, T> pendingValue = pendingValue();
		if (bypass || listenerTriggerDepth == 0) {
			if (listenerTriggerDepth > 10) {
				throw new IllegalStateException(
						"Listener trigger depth exceeded 10! This means a listener triggered a listener etc etc 10 times deep. This is likely a bug in the mod using YACL!");
			}

			this.listenerTriggerDepth++;

			for (BiConsumer<Option<Map<S, T>>, Map<S, T>> listener : listeners) {
				try {
					listener.accept(this, pendingValue);
				} catch (Exception e) {
					YACLConstants.LOGGER.error("Exception whilst triggering listener for option '%s'".formatted(name.getString()), e);
				}
			}

			this.listenerTriggerDepth--;
		}
	}

	private void onRefresh() {
		refreshListeners.forEach(Runnable::run);
		callListeners(true);
	}

	private class EntryFactory {
		private final Function<MapOptionEntry<S, T>, Controller<S>> keyControllerFunction;
		private final Function<MapOptionEntry<S, T>, Controller<T>> valueControllerFunction;

		private EntryFactory(Function<MapOptionEntry<S, T>, Controller<S>> keyControllerFunction, Function<MapOptionEntry<S, T>, Controller<T>> valueControllerFunction) {
			this.keyControllerFunction = keyControllerFunction;
			this.valueControllerFunction = valueControllerFunction;
		}

		public MapOptionEntry<S, T> create(Map.Entry<S, T> initialValue) {
			return new MapOptionEntryImpl<>(MapOptionImpl.this, initialValue, keyControllerFunction, valueControllerFunction);
		}
	}

	public static final class BuilderImpl<S, T> implements Builder<S, T> {
		private Component name = Component.empty();
		private OptionDescription description = OptionDescription.EMPTY;
		private Function<MapOptionEntry<S, T>, Controller<S>> keyControllerFunction;
		private Function<MapOptionEntry<S, T>, Controller<T>> valueControllerFunction;
		private Binding<Map<S, T>> binding = null;
		private final Set<OptionFlag> flags = new HashSet<>();
		private Supplier<Map.Entry<S, T>> initialValue;
		private boolean collapsed = false;
		private boolean available = true;
		private int minimumNumberOfEntries = 0;
		private int maximumNumberOfEntries = Integer.MAX_VALUE;
		private boolean insertEntriesAtEnd = false;
		private final List<BiConsumer<Option<Map<S, T>>, Map<S, T>>> listeners = new ArrayList<>();

		@Override
		public Builder<S, T> name(@NotNull Component name) {
			Validate.notNull(name, "`name` must not be null");

			this.name = name;
			return this;
		}

		@Override
		public Builder<S, T> description(@NotNull OptionDescription description) {
			Validate.notNull(description, "`description` must not be null");

			this.description = description;
			return this;
		}

		@Override
		public Builder<S, T> initial(@NotNull Supplier<Map.Entry<S, T>> initialValue) {
			Validate.notNull(initialValue, "`initialValue` cannot be empty");

			this.initialValue = initialValue;
			return this;
		}

		@Override
		public Builder<S, T> initial(@NotNull Map.Entry<S, T> initialValue) {
			Validate.notNull(initialValue, "`initialValue` cannot be empty");

			this.initialValue = () -> initialValue;
			return this;
		}

		@Override
		public Builder<S, T> keyController(@NotNull Function<MapOptionEntry<S, T>, ControllerBuilder<S>> controller) {
			Validate.notNull(controller, "`controller` cannot be null");

			this.keyControllerFunction = opt -> controller.apply(opt).build();
			return this;
		}

		@Override
		public Builder<S, T> valueController(@NotNull Function<MapOptionEntry<S, T>, ControllerBuilder<T>> controller) {
			Validate.notNull(controller, "`controller` cannot be null");

			this.valueControllerFunction = opt -> controller.apply(opt).build();
			return this;
		}

		@Override
		public Builder<S, T> customController(@NotNull Function<MapOptionEntry<S, T>, Controller<S>> keyControl, @NotNull Function<MapOptionEntry<S, T>, Controller<T>> valueControl) {
			Validate.notNull(keyControl, "`keyControl` cannot be null");
			Validate.notNull(valueControl, "`valueControl` cannot be null");

			this.keyControllerFunction = keyControl;
			this.valueControllerFunction = valueControl;
			return this;
		}

		@Override
		public Builder<S, T> binding(@NotNull Binding<Map<S, T>> binding) {
			Validate.notNull(binding, "`binding` cannot be null");

			this.binding = binding;
			return this;
		}

		@Override
		public Builder<S, T> binding(@NotNull Map<S, T> def, @NotNull Supplier<@NotNull Map<S, T>> getter, @NotNull Consumer<@NotNull Map<S, T>> setter) {
			Validate.notNull(def, "`def` must not be null");
			Validate.notNull(getter, "`getter` must not be null");
			Validate.notNull(setter, "`setter` must not be null");

			this.binding = Binding.generic(def, getter, setter);
			return this;
		}

		@Override
		public Builder<S, T> available(boolean available) {
			this.available = available;
			return this;
		}

		@Override
		public Builder<S, T> minimumNumberOfEntries(int number) {
			this.minimumNumberOfEntries = number;
			return this;
		}

		@Override
		public Builder<S, T> maximumNumberOfEntries(int number) {
			this.maximumNumberOfEntries = number;
			return this;
		}

		@Override
		public Builder<S, T> insertEntriesAtEnd(boolean insertAtEnd) {
			this.insertEntriesAtEnd = insertAtEnd;
			return this;
		}

		@Override
		public Builder<S, T> flag(@NotNull OptionFlag... flag) {
			Validate.notNull(flag, "`flag` must not be null");

			this.flags.addAll(Arrays.asList(flag));
			return this;
		}

		@Override
		public Builder<S, T> flags(@NotNull Collection<OptionFlag> flags) {
			Validate.notNull(flags, "`flags` must not be null");

			this.flags.addAll(flags);
			return this;
		}

		@Override
		public Builder<S, T> collapsed(boolean collapsible) {
			this.collapsed = collapsible;
			return this;
		}

		@Override
		public Builder<S, T> listener(@NotNull BiConsumer<Option<Map<S, T>>, Map<S, T>> listener) {
			this.listeners.add(listener);
			return this;
		}

		@Override
		public Builder<S, T> listeners(@NotNull Collection<BiConsumer<Option<Map<S, T>>, Map<S, T>>> listeners) {
			this.listeners.addAll(listeners);
			return this;
		}

		@Override
		public MapOption<S, T> build() {
			Validate.notNull(keyControllerFunction, "`keyController` must not be null");
			Validate.notNull(valueControllerFunction, "`valueController` must not be null");
			Validate.notNull(binding, "`binding` must not be null");
			Validate.notNull(initialValue, "`initialValue` must not be null");

			return new MapOptionImpl<>(name, description, binding, initialValue, keyControllerFunction, valueControllerFunction,
					ImmutableSet.copyOf(flags), collapsed, available, minimumNumberOfEntries, maximumNumberOfEntries, insertEntriesAtEnd,
					listeners
			);
		}
	}
}
