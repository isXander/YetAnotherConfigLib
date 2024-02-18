package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.*;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class HiddenNameMapOptionEntry<S, T> implements MapOptionEntry<S, T> {
	private final MapOptionEntry<S, T> option;

	public HiddenNameMapOptionEntry(MapOptionEntry<S, T> option) {
		this.option = option;
	}

	@Override
	public @NotNull Component name() {
		return Component.empty();
	}

	@Override
	public @NotNull OptionDescription description() {
		return option.description();
	}

	@Override
	@Deprecated
	public @NotNull Component tooltip() {
		return option.tooltip();
	}

	@Override
	public @NotNull Controller<T> controller() {
		return option.controller();
	}

	@Override
	public @NotNull Binding<T> binding() {
		return option.binding();
	}

	@Override
	public boolean available() {
		return option.available();
	}

	@Override
	public void setAvailable(boolean available) {
		option.setAvailable(available);
	}

	@Override
	public MapOption<S, T> parentGroup() {
		return option.parentGroup();
	}

	@Override
	public @NotNull ImmutableSet<OptionFlag> flags() {
		return option.flags();
	}

	@Override
	public boolean changed() {
		return option.changed();
	}

	@Override
	public @NotNull T pendingValue() {
		return option.pendingValue();
	}

	@Override
	public void requestSet(@NotNull T value) {
		option.requestSet(value);
	}

	@Override
	public boolean applyValue() {
		return option.applyValue();
	}

	@Override
	public void forgetPendingValue() {
		option.forgetPendingValue();
	}

	@Override
	public void requestSetDefault() {
		option.requestSetDefault();
	}

	@Override
	public boolean isPendingValueDefault() {
		return option.isPendingValueDefault();
	}

	@Override
	public boolean canResetToDefault() {
		return option.canResetToDefault();
	}

	@Override
	public void addListener(BiConsumer<Option<T>, T> changedListener) {
		option.addListener(changedListener);
	}
}
