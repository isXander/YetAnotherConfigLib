package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.impl.ListOptionImpl;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A list option that takes form as an option group for UX.
 * You add this option through {@link ConfigCategory.Builder#group(OptionGroup)}. Do NOT add as an option.
 * Users can add remove and reshuffle a list type. You can use any controller you wish, there are no dedicated
 * controllers for list types. List options do not manipulate your list but get and set the list with a
 * regular binding for simplicity.
 *
 * You may apply option flags like a normal option and collapse like a normal group, it is a merge of them both.
 * Methods in this interface marked with {@link ApiStatus.Internal} should not be used, and could be subject to
 * change at any time
 * @param <T>
 */
public interface ListOption<T> extends OptionGroup, Option<List<T>> {
    @Override
    @NotNull ImmutableList<ListOptionEntry<T>> options();

    @ApiStatus.Internal
    int numberOfEntries();

    @ApiStatus.Internal
    int maximumNumberOfEntries();

    @ApiStatus.Internal
    int minimumNumberOfEntries();

    @ApiStatus.Internal
    ListOptionEntry<T> insertNewEntryToTop();

    @ApiStatus.Internal
    void insertEntry(int index, ListOptionEntry<?> entry);

    @ApiStatus.Internal
    int indexOf(ListOptionEntry<?> entry);

    @ApiStatus.Internal
    void removeEntry(ListOptionEntry<?> entry);

    @ApiStatus.Internal
    void addRefreshListener(Runnable changedListener);

    static <T> Builder<T> createBuilder() {
        return new ListOptionImpl.BuilderImpl<>();
    }

    @Deprecated
    static <T> Builder<T> createBuilder(Class<T> typeClass) {
        return createBuilder();
    }

    interface Builder<T> {
        /**
         * Sets name of the list, for UX purposes, a name should always be given,
         * but isn't enforced.
         *
         * @see ListOption#name()
         */
        Builder<T> name(@NotNull Component name);

        Builder<T> description(@NotNull OptionDescription description);

        /**
         * Sets the value that is used when creating new entries
         */
        Builder<T> initial(@NotNull T initialValue);

        Builder<T> controller(@NotNull Function<Option<T>, ControllerBuilder<T>> controller);

        /**
         * Sets the controller for the option.
         * This is how you interact and change the options.
         *
         * @see dev.isxander.yacl3.gui.controllers
         */
        Builder<T> customController(@NotNull Function<ListOptionEntry<T>, Controller<T>> control);

        /**
         * Sets the binding for the option.
         * Used for default, getter and setter.
         *
         * @see Binding
         */
        Builder<T> binding(@NotNull Binding<List<T>> binding);

        /**
         * Sets the binding for the option.
         * Shorthand of {@link Binding#generic(Object, Supplier, Consumer)}
         *
         * @param def default value of the option, used to reset
         * @param getter should return the current value of the option
         * @param setter should set the option to the supplied value
         * @see Binding
         */
        Builder<T> binding(@NotNull List<T> def, @NotNull Supplier<@NotNull List<T>> getter, @NotNull Consumer<@NotNull List<T>> setter);

        /**
         * Sets if the option can be configured
         *
         * @see Option#available()
         */
        Builder<T> available(boolean available);

        /**
         * Sets a minimum size for the list. Once this size is reached,
         * no further entries may be removed.
         */
        Builder<T> minimumNumberOfEntries(int number);

        /**
         * Sets a maximum size for the list. Once this size is reached,
         * no further entries may be added.
         */
        Builder<T> maximumNumberOfEntries(int number);

        /**
         * Adds a flag to the option.
         * Upon applying changes, all flags are executed.
         * {@link Option#flags()}
         */
        Builder<T> flag(@NotNull OptionFlag... flag);

        /**
         * Adds a flag to the option.
         * Upon applying changes, all flags are executed.
         * {@link Option#flags()}
         */
        Builder<T> flags(@NotNull Collection<OptionFlag> flags);

        /**
         * Dictates if the group should be collapsed by default.
         * If not set, it will not be collapsed by default.
         *
         * @see OptionGroup#collapsed()
         */
        Builder<T> collapsed(boolean collapsible);

        /**
         * Adds a listener to the option. Invoked upon changing any of the list's entries.
         *
         * @see Option#addListener(BiConsumer)
         */
        ListOption.Builder<T> listener(@NotNull BiConsumer<Option<List<T>>, List<T>> listener);

        /**
         * Adds multiple listeners to the option. Invoked upon changing of any of the list's entries.
         *
         * @see Option#addListener(BiConsumer)
         */
        ListOption.Builder<T> listeners(@NotNull Collection<BiConsumer<Option<List<T>>, List<T>>> listeners);

        ListOption<T> build();
    }
}
