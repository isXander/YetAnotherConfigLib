package dev.isxander.yacl.gui.controllers.string;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.text.Text;

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
    default Text formatValue() {
        return Text.of(getString());
    }

    default boolean isInputValid(String input) {
        return true;
    }

    @Override
    default AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new StringControllerElement(this, screen, widgetDimension, true);
    }
}
