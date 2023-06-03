package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.IntegerFieldControllerBuilderImpl;

public interface IntegerFieldControllerBuilder extends NumberFieldControllerBuilder<Integer, IntegerFieldControllerBuilder> {
    static IntegerFieldControllerBuilder create(Option<Integer> option) {
        return new IntegerFieldControllerBuilderImpl(option);
    }
}
