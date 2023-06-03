package dev.isxander.yacl3.gui.controllers.slider;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;

/**
 * Simple custom slider implementation that shifts the current value across when shown.
 * <p>
 * For simplicity, {@link SliderControllerElement} works in doubles so each
 * {@link ISliderController} must cast to double. This is to get around re-writing the element for every type.
 */
public interface ISliderController<T extends Number> extends Controller<T> {
    /**
     * Gets the minimum value for the slider
     */
    double min();

    /**
     * Gets the maximum value for the slider
     */
    double max();

    /**
     * Gets the interval (or step size) for the slider.
     */
    double interval();

    /**
     * Gets the range of the slider.
     */
    default double range() {
        return max() - min();
    }

    /**
     * Sets the {@link dev.isxander.yacl3.api.Option}'s pending value
     */
    void setPendingValue(double value);

    /**
     * Gets the {@link dev.isxander.yacl3.api.Option}'s pending value
     */
    double pendingValue();

    /**
     * {@inheritDoc}
     */
    @Override
    default AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new SliderControllerElement(this, screen, widgetDimension, min(), max(), interval());
    }
}
