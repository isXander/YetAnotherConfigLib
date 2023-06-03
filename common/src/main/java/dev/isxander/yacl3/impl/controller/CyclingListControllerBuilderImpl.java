package dev.isxander.yacl3.impl.controller;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.gui.controllers.cycling.CyclingListController;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public final class CyclingListControllerBuilderImpl<T> extends AbstractControllerBuilderImpl<T> implements CyclingListControllerBuilder<T> {
    private Iterable<? extends T> values;
    private Function<T, Component> formatter = null;

    public CyclingListControllerBuilderImpl(Option<T> option) {
        super(option);
    }

    @Override
    public CyclingListControllerBuilder<T> values(Iterable<? extends T> values) {
        this.values = values;
        return this;
    }

    @SafeVarargs
    @Override
    public final CyclingListControllerBuilder<T> values(T... values) {
        this.values = ImmutableList.copyOf(values);
        return this;
    }

    @Override
    public CyclingListControllerBuilder<T> valueFormatter(Function<T, Component> formatter) {
        this.formatter = formatter;
        return this;
    }

    @Override
    public Controller<T> build() {
        return new CyclingListController<>(option, values, formatter);
    }
}
