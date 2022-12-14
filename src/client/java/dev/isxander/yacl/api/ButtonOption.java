package dev.isxander.yacl.api;

import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.ButtonOptionImpl;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ButtonOption extends Option<BiConsumer<YACLScreen, ButtonOption>> {
    /**
     * Action to be executed upon button press
     */
    BiConsumer<YACLScreen, ButtonOption> action();

    static Builder createBuilder() {
        return new ButtonOptionImpl.BuilderImpl();
    }

    interface Builder {
        /**
         * Sets the name to be used by the option.
         *
         * @see Option#name()
         */
        Builder name(@NotNull Text name);

        /**
         * Sets the tooltip to be used by the option.
         * Can be invoked twice to append more lines.
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltips text lines - merged with a new-line on {@link Option.Builder#build()}.
         */
        Builder tooltip(@NotNull Text... tooltips);

        Builder action(@NotNull BiConsumer<YACLScreen, ButtonOption> action);

        /**
         * Action to be executed upon button press
         *
         * @see ButtonOption#action()
         */
        @Deprecated
        Builder action(@NotNull Consumer<YACLScreen> action);

        /**
         * Sets if the option can be configured
         *
         * @see Option#available()
         */
        Builder available(boolean available);

        /**
         * Sets the controller for the option.
         * This is how you interact and change the options.
         *
         * @see dev.isxander.yacl.gui.controllers
         */
        Builder controller(@NotNull Function<ButtonOption, Controller<BiConsumer<YACLScreen, ButtonOption>>> control);

        ButtonOption build();
    }
}
