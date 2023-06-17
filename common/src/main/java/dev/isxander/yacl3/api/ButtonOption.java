package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.ButtonOptionImpl;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        Builder name(@NotNull Component name);

        /**
         * Sets the button text to be displayed next to the name.
         */
        Builder text(@NotNull Component text);

        Builder description(@NotNull OptionDescription description);

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

        ButtonOption build();
    }
}
