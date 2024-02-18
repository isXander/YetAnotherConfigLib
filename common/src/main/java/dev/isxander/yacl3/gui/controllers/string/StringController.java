package dev.isxander.yacl3.gui.controllers.string;

import dev.isxander.yacl3.api.MapOptionEntry;
import dev.isxander.yacl3.api.Option;

import java.util.Map;

/**
 * A custom text field implementation for strings.
 */
public class StringController implements IStringController<String> {
    private final Option<String> option;

    /**
     * Constructs a string controller
     *
     * @param option bound option
     */
    public StringController(Option<String> option) {
        this.option = option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<String> option() {
        return option;
    }

    @Override
    public String getString() {
        // TODO: How should this be handled?
        if (option() instanceof MapOptionEntry<?, ?> mapOptionEntry) {
            return ((Map.Entry<?, ?>) mapOptionEntry.pendingValue()).getKey().toString();
        }

        return option().pendingValue();
    }

    @Override
    public void setFromString(String value) {
        option().requestSet(value);
    }
}
