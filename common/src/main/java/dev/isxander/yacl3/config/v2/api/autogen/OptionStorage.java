package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.Option;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface OptionStorage {
    @Nullable Option<?> getOption(String fieldName);

    void scheduleOptionOperation(String fieldName, Consumer<Option<?>> optionConsumer);
}
