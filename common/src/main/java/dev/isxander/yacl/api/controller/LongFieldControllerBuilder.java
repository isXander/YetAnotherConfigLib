package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.LongFieldControllerBuilderImpl;

public interface LongFieldControllerBuilder extends NumberFieldControllerBuilder<Long, LongFieldControllerBuilder> {
    static LongFieldControllerBuilder create(Option<Long> option) {
        return new LongFieldControllerBuilderImpl(option);
    }
}
