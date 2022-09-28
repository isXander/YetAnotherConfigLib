package dev.isxander.yacl.gui.controllers.cycling;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;

public interface ICyclingController<T> extends Controller<T> {
    void setPendingValue(int ordinal);

    int getPendingValue();

    int getCycleLength();

    @Override
    default AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new CyclingControllerElement(this, screen, widgetDimension);
    }
}
