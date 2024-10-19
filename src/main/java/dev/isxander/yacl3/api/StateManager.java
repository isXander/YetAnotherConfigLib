package dev.isxander.yacl3.api;

import dev.isxander.yacl3.impl.ImmutableStateManager;
import dev.isxander.yacl3.impl.InstantStateManager;
import dev.isxander.yacl3.impl.SimpleStateManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface StateManager<T> {
    static <T> StateManager<T> createSimple(Binding<T> binding) {
        return new SimpleStateManager<>(binding);
    }

    static <T> StateManager<T> createSimple(@NotNull T def, @NotNull Supplier<@NotNull T> getter, @NotNull Consumer<@NotNull T> setter) {
        return new SimpleStateManager<>(Binding.generic(def, getter, setter));
    }

    static <T> StateManager<T> createInstant(Binding<T> binding) {
        return new InstantStateManager<>(binding);
    }

    static <T> StateManager<T> createInstant(@NotNull T def, @NotNull Supplier<@NotNull T> getter, @NotNull Consumer<@NotNull T> setter) {
        return new InstantStateManager<>(Binding.generic(def, getter, setter));
    }

    static <T> StateManager<T> createImmutable(@NotNull T value) {
        return new ImmutableStateManager<>(value);
    }

    /**
     * Sets the pending value.
     */
    void set(T value);

    /**
     * @return the pending value.
     */
    T get();

    /**
     * Applies the pending value to the backed binding.
     */
    void apply();

    void resetToDefault(ResetAction action);

    /**
     * Essentially "forgets" the pending value and reassigns state as backed by the binding.
     */
    void sync();

    /**
     * @return true if the pending value is the same as the backed binding value.
     */
    boolean isSynced();

    /**
     * @return true if this state manage will always be synced with the backing binding.
     */
    default boolean isAlwaysSynced() {
        return false;
    }

    boolean isDefault();

    void addListener(StateListener<T> stateListener);

    enum ResetAction {
        BY_OPTION,
        BY_GLOBAL,
    }

    interface StateListener<T> {
        static <T> StateListener<T> noop() {
            return (oldValue, newValue) -> {};
        }

        void onStateChange(T oldValue, T newValue);

        default StateListener<T> andThen(StateListener<T> after) {
            return (oldValue, newValue) -> {
                this.onStateChange(oldValue, newValue);
                after.onStateChange(oldValue, newValue);
            };
        }
    }
}
