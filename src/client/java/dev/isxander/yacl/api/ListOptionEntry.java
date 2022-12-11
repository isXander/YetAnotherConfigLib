package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

public interface ListOptionEntry<T> extends Option<T> {
    ListOption<T> parentGroup();

    @Override
    default @NotNull Class<T> typeClass() {
        return parentGroup().elementTypeClass();
    }

    @Override
    default @NotNull ImmutableSet<OptionFlag> flags() {
        return parentGroup().flags();
    }

    @Override
    default boolean available() {
        return parentGroup().available();
    }
}
