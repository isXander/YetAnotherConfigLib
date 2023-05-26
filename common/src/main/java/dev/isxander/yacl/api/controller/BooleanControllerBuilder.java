package dev.isxander.yacl.api.controller;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.impl.controller.BooleanControllerBuilderImpl;

public interface BooleanControllerBuilder extends ValueFormattableController<Boolean, BooleanControllerBuilder> {
    BooleanControllerBuilder coloured(boolean coloured);

    BooleanControllerBuilder onOffFormatter();
    BooleanControllerBuilder yesNoFormatter();
    BooleanControllerBuilder trueFalseFormatter();

    static BooleanControllerBuilder create(Option<Boolean> option) {
        return new BooleanControllerBuilderImpl(option);
    }
}
