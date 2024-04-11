package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.Option;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * An accessor to all options that are auto-generated
 * by the config system.
 */
public interface OptionAccess {
    /**
     * Gets an option by its field name.
     * This could be null if the option hasn't been created yet. It is created
     * in order of the fields in the class, so if you are trying to get an option
     * lower-down in the class, this will return null.
     *
     * @param fieldName the exact, case-sensitive name of the field.
     * @return the created option, or {@code null} if it hasn't been created yet.
     */
    @Nullable Option<?> getOption(String fieldName);

    /**
     * Schedules an operation to be performed on an option.
     * If the option has already been created, the consumer will be
     * accepted immediately upon calling this method, if not, it will
     * be added to the queue of operations to be performed on the option
     * once it does get created.
     *
     * @param fieldName the exact, case-sensitive name of the field.
     * @param optionConsumer the operation to perform on the option.
     */
    void scheduleOptionOperation(String fieldName, Consumer<Option<?>> optionConsumer);
}
