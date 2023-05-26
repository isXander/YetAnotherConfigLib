package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.LabelOptionImpl;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A label option is an easier way of creating a label with a {@link dev.isxander.yacl.gui.controllers.LabelController}.
 * This option is immutable and cannot be disabled. Tooltips are supported through
 * {@link Component} styling.
 */
public interface LabelOption extends Option<Component> {
    @NotNull Component label();

    /**
     * Creates a new label option with the given label, skipping a builder for ease.
     */
    static LabelOption create(@NotNull Component label) {
        return new LabelOptionImpl(label);
    }

    static Builder createBuilder() {
        return new LabelOptionImpl.BuilderImpl();
    }

    interface Builder {
        /**
         * Appends a line to the label
         */
        Builder line(@NotNull Component line);

        /**
         * Appends multiple lines to the label
         */
        Builder lines(@NotNull Collection<? extends Component> lines);

        LabelOption build();
    }
}
