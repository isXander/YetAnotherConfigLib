package dev.isxander.yacl.api.utils;

import dev.isxander.yacl.api.*;

import java.util.function.Consumer;
import java.util.function.Function;

public class OptionUtils {
    /**
     * Consumes all options, ignoring groups and categories.
     * When consumer returns true, this function stops iterating.
     */
    public static void consumeOptions(YetAnotherConfigLib yacl, Function<Option<?>, Boolean> consumer) {
        for (ConfigCategory category : yacl.categories()) {
            for (OptionGroup group : category.groups()) {
                if (group instanceof ListOption<?> list) {
                    if (consumer.apply(list)) return;
                } else {
                    for (Option<?> option : group.options()) {
                        if (consumer.apply(option)) return;
                    }
                }

            }
        }
    }

    /**
     * Consumes all options, ignoring groups and categories.
     *
     * @see OptionUtils#consumeOptions(YetAnotherConfigLib, Function)
     */
    public static void forEachOptions(YetAnotherConfigLib yacl, Consumer<Option<?>> consumer) {
        consumeOptions(yacl, (opt) -> {
            consumer.accept(opt);
            return false;
        });
    }
}
