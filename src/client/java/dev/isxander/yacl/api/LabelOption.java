package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.LabelOptionImpl;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A label option is an easier way of creating a label with a {@link dev.isxander.yacl.gui.controllers.LabelController}.
 * This option is immutable and cannot be disabled. Tooltips are supported through
 * {@link Text} styling.
 */
public interface LabelOption extends Option<Text> {
    @NotNull Text label();

    /**
     * Creates a new label option with the given label, skipping a builder for ease.
     */
    static LabelOption create(@NotNull Text label) {
        return new LabelOptionImpl(label);
    }

    static Builder createBuilder() {
        return new LabelOptionImpl.BuilderImpl();
    }

    interface Builder {
        /**
         * Appends a line to the label
         */
        Builder line(@NotNull Text line);

        /**
         * Appends multiple lines to the label
         */
        Builder lines(@NotNull Collection<? extends Text> lines);

        LabelOption build();
    }
}
