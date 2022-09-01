package dev.isxander.yacl.gui.controllers.slider;

import dev.isxander.yacl.api.Control;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.controllers.ControlWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public interface ISliderControl<T extends Number> extends Control<T> {
    double min();

    double max();

    double interval();

    default double range() {
        return max() - min();
    }

    void setPendingValue(double value);
    double pendingValue();

    Text getValueText(double value);

    @Override
    default ControlWidget<?> provideWidget(Screen screen, Dimension<Integer> widgetDimension) {
        return new SliderControlElement(this, screen, widgetDimension, min(), max(), interval());
    }
}
