package dev.isxander.yacl3.api;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Supplier;

public interface OptionAddable {
    /**
     * Adds an option to an abstract builder.
     * To construct an option, use {@link Option#createBuilder()}
     */
    OptionAddable option(@NotNull Option<?> option);

    /**
     * Adds an option to an abstract builder.
     * To construct an option, use {@link Option#createBuilder()}
     * @param optionSupplier to be called to initialise the option. Called immediately.
     */
    default OptionAddable option(@NotNull Supplier<@NotNull Option<?>> optionSupplier) {
        return option(optionSupplier.get());
    }

    /**
     * Adds an option to an abstract builder if a condition is met.
     * To construct an option, use {@link Option#createBuilder()}
     * @param condition whether to add the option
     * @param option the option to add
     * @return this
     */
    default OptionAddable optionIf(boolean condition, @NotNull Option<?> option) {
        return condition ? option(option) : this;
    }

    /**
     * Adds an option to an abstract builder if a condition is met.
     * To construct an option, use {@link Option#createBuilder()}
     * @param condition whether to add the option
     * @param optionSupplier to be called to initialise the option. Called immediately if and only if condition is true.
     * @return this
     */
    default OptionAddable optionIf(boolean condition, @NotNull Supplier<@NotNull Option<?>> optionSupplier) {
        return condition ? option(optionSupplier) : this;
    }

    /**
     * Adds multiple options to an abstract builder.
     * To construct an option, use {@link Option#createBuilder()}
     */
    OptionAddable options(@NotNull Collection<? extends Option<?>> options);
}
