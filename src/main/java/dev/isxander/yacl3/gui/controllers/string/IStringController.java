package dev.isxander.yacl3.gui.controllers.string;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;

/**
 * A controller that can be any type but can input and output a string.
 */
public interface IStringController<T> extends Controller<T> {
    /**
     * Gets the option's pending value as a string.
     *
     * @see Option#pendingValue()
     */
    String getString();

    /**
     * Sets the option's pending value from a string.
     *
     * @see Option#requestSet(Object)
     */
    void setFromString(String value);

    /**
     * {@inheritDoc}
     */
    @Override
    default Component formatValue() {
        return Component.literal(getString());
    }

    default boolean isInputValid(String input) {
        return true;
    }

    @Override
    default AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new StringControllerElement(this, screen, widgetDimension, true);
    }
}
