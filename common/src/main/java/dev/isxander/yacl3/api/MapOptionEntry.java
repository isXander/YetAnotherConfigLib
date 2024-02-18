package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

public interface MapOptionEntry<S, T> extends Option<T> {
	MapOption<S, T> parentGroup();

	@Override
	default @NotNull ImmutableSet<OptionFlag> flags() {
		return parentGroup().flags();
	}

	@Override
	default boolean available() {
		return parentGroup().available();
	}
}
