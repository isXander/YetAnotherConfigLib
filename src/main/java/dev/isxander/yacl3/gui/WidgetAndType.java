package dev.isxander.yacl3.gui;

import net.minecraft.client.gui.components.AbstractWidget;

public interface WidgetAndType<T> {
    T getType();

    AbstractWidget getWidget();

    static <T extends AbstractWidget> WidgetAndType<T> ofWidget(T widget) {
        return new WidgetAndType<>() {
            @Override
            public T getType() {
                return widget;
            }

            @Override
            public AbstractWidget getWidget() {
                return widget;
            }
        };
    }
}
