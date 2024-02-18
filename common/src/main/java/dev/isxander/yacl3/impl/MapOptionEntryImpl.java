package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.MapEntryWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MapOptionEntryImpl<S, T> implements MapOptionEntry<S, T> {
	private final MapOptionImpl<S, T> group;

	private Map.Entry<S, T> value;

	private final Binding<T> binding;
	private final Controller<T> controller;

	MapOptionEntryImpl(MapOptionImpl<S, T> group, Map.Entry<S, T> initialValue, @NotNull Function<MapOptionEntry<S, T>, Controller<S>> keyControlGetter, @NotNull Function<MapOptionEntry<S, T>, Controller<T>> valueControlGetter) {
		this.group = group;
		this.value = initialValue;
		this.binding = new EntryBinding();
		this.controller = new EntryController<>(
				keyControlGetter.apply(new HiddenNameMapOptionEntry<>(this)),
				valueControlGetter.apply(new HiddenNameMapOptionEntry<>(this)), this
		);
	}

	@Override
	public MapOption<S, T> parentGroup() {
		return group;
	}

	@Override
	public @NotNull Component name() {
		return group.name();
	}

	@Override
	public @NotNull OptionDescription description() {
		return group.description();
	}

	@Override
	public @NotNull Component tooltip() {
		return group.tooltip();
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
	public void setAvailable(boolean available) {}

	@Override
	public boolean changed() {
		return false;
	}

	@Override
	public @NotNull T pendingValue() {
		return (T) value;
	}

	@Override
	public void requestSet(@NotNull T value) {
		binding.setValue(value);
	}

	@Override
	public boolean applyValue() {
		return false;
	}

	@Override
	public void forgetPendingValue() {}

	@Override
	public void requestSetDefault() {}

	@Override
	public boolean isPendingValueDefault() {
		return false;
	}

	@Override
	public void addListener(BiConsumer<Option<T>, T> changedListener) {}

	/**
	 * Open in case mods need to find the real controller type.
	 */
	@ApiStatus.Internal
	public record EntryController<S, T>(Controller<S> keyController, Controller<T> valueController,
	                                    MapOptionEntryImpl<S, T> entry) implements Controller<T> {
		@Override
		public Option<T> option() {
			return valueController.option();
		}

		@Override
		public Component formatValue() {
			return keyController.formatValue();
		}

		@Override
		public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
			// TODO: Update widget to include map values alongside keys
			return new MapEntryWidget(screen, entry, keyController.provideWidget(screen, widgetDimension));
		}
	}

	private class EntryBinding implements Binding<T> {
		@Override
		public void setValue(T newValue) {
			value = (Map.Entry<S, T>) newValue;
			group.callListeners(true);
		}

		@Override
		public T getValue() {
			return (T) value;
		}

		@Override
		public T defaultValue() {
			throw new UnsupportedOperationException();
		}
	}
}
