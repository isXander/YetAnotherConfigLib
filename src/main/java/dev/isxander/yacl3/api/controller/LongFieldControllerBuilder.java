package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.LongFieldControllerBuilderImpl;

public interface LongFieldControllerBuilder extends NumberFieldControllerBuilder<Long, LongFieldControllerBuilder> {
    static LongFieldControllerBuilder create(Option<Long> option) {
        return new LongFieldControllerBuilderImpl(option);
    }
}
