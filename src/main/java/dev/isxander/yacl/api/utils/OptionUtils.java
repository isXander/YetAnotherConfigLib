package dev.isxander.yacl.api.utils;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.YetAnotherConfigLib;

import java.util.function.Consumer;
import java.util.function.Function;

public class OptionUtils {
    public static void consumeOptions(YetAnotherConfigLib yacl, Function<Option<?>, Boolean> consumer) {
        for (ConfigCategory category : yacl.categories()) {
            for (OptionGroup group : category.groups()) {
                for (Option<?> option : group.options()) {
                    if (!consumer.apply(option)) return;
                }
            }
        }
    }

    public static void forEachOptions(YetAnotherConfigLib yacl, Consumer<Option<?>> consumer) {
        consumeOptions(yacl, (opt) -> {
            consumer.accept(opt);
            return true;
        });
    }
}
