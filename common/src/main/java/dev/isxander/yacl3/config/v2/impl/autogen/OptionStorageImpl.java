package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.autogen.OptionStorage;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OptionStorageImpl implements OptionStorage {
    private final Map<String, Option<?>> storage = new HashMap<>();
    private final Map<String, Consumer<Option<?>>> scheduledOperations = new HashMap<>();

    @Override
    public @Nullable Option<?> getOption(String fieldName) {
        return storage.get(fieldName);
    }

    @Override
    public void scheduleOptionOperation(String fieldName, Consumer<Option<?>> optionConsumer) {
        if (storage.containsKey(fieldName)) {
            optionConsumer.accept(storage.get(fieldName));
        } else {
            scheduledOperations.merge(fieldName, optionConsumer, Consumer::andThen);
        }
    }

    public void putOption(String fieldName, Option<?> option) {
        storage.put(fieldName, option);

        Consumer<Option<?>> consumer = scheduledOperations.remove(fieldName);
        if (consumer != null) {
            consumer.accept(option);
        }
    }
}
