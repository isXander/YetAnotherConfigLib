package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.BooleanControllerBuilderImpl;

public interface BooleanControllerBuilder extends ValueFormattableController<Boolean, BooleanControllerBuilder> {
    BooleanControllerBuilder coloured(boolean coloured);

    BooleanControllerBuilder onOffFormatter();
    BooleanControllerBuilder yesNoFormatter();
    BooleanControllerBuilder trueFalseFormatter();

    static BooleanControllerBuilder create(Option<Boolean> option) {
        return new BooleanControllerBuilderImpl(option);
    }
}
