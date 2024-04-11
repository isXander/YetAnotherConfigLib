package dev.isxander.yacl3.api;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;

/**
 * Provides a widget to control the option.
 */
public interface Controller<T> {
    /**
     * Gets the dedicated {@link Option} for this controller
     */
    Option<T> option();

    /**
     * Gets the formatted value based on {@link Option#pendingValue()}
     */
    Component formatValue();

    /**
     * Provides a widget to display
     *
     * @param screen parent screen
     */
    AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension);
}
