package dev.isxander.yacl.gui.controllers.string;

import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;

/**
 * A custom text field implementation for strings.
 */
public class StringController implements IStringController<String> {
    private final Option<String, ?> option;

    /**
     * Constructs a string controller
     *
     * @param option bound option
     */
    public StringController(Option<String, ?> option) {
        this.option = option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<String, ?> option() {
        return option;
    }

    @Override
    public String getString() {
        return option().pendingValue();
    }

    @Override
    public void setFromString(String value) {
        option().requestSet(value);
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new StringControllerElement(this, screen, widgetDimension);
    }
}
