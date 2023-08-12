package dev.isxander.yacl3.config.v2.api;

import dev.isxander.yacl3.api.Option;

public interface OptionFactory<T> {
    Option<T> create(ConfigField<T> field);

    Class<T> type();
}
