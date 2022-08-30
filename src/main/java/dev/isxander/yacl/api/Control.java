package dev.isxander.yacl.api;

import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;

public interface Control<T> {
    Option<T> option();

    AbstractWidget provideWidget(Dimension<Integer> widgetDimension);
}
