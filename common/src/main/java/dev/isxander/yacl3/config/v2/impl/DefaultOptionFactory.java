package dev.isxander.yacl3.config.v2.impl;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.OptionFactory;
import org.apache.commons.lang3.NotImplementedException;

public class DefaultOptionFactory implements OptionFactory<Object> {
    @Override
    public Option<Object> create(ConfigField<Object> field) {
        throw new NotImplementedException();
    }

    @Override
    public Class<Object> type() {
        throw new NotImplementedException();
    }
}
