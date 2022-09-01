package dev.isxander.yacl.api;

import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

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
    Text formatValue();

    /**
     * Provides a widget to display
     *
     * @param screen parent screen
     */
    @ApiStatus.Internal
    ControllerWidget<?> provideWidget(Screen screen, Dimension<Integer> widgetDimension);
}
