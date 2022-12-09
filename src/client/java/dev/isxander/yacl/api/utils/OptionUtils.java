package dev.isxander.yacl.api.utils;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;

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
                for (Option<?> option : group.options()) {
                    if (consumer.apply(option)) return;
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
