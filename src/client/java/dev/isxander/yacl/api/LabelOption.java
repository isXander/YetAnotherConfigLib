package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.LabelOptionImpl;
import net.minecraft.text.Text;

/**
 * A label option is an easier way of creating a label with a {@link dev.isxander.yacl.gui.controllers.LabelController}.
 * This option is immutable and cannot be disabled. Tooltips are supported through
 * {@link Text} styling.
 */
public interface LabelOption extends Option<Text> {
    Text label();

    /**
     * Creates a new label option with the given label.
     */
    static LabelOption create(Text label) {
        return new LabelOptionImpl(label);
    }
}
