package dev.isxander.yacl.api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface OptionAddable {
    /**
     * Adds an option to an abstract builder.
     * To construct an option, use {@link Option#createBuilder(Class)}
     */
    OptionAddable option(@NotNull Option<?> option);

    /**
     * Adds multiple options to an abstract builder.
     * To construct an option, use {@link Option#createBuilder(Class)}
     */
    OptionAddable options(@NotNull Collection<? extends Option<?>> options);
}
