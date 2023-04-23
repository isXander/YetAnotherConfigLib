package dev.isxander.yacl.api;

import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.ButtonOptionImpl;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ButtonOption extends Option<BiConsumer<YACLScreen, ButtonOption>> {
    /**
     * Action to be executed upon button press
     */
    BiConsumer<YACLScreen, ButtonOption> action();

    static dev.isxander.yacl.api.ButtonOption.Builder createBuilder() {
        return new ButtonOptionImpl.BuilderImpl();
    }

    interface Builder {
        /**
         * Sets the name to be used by the option.
         *
         * @see Option#name()
         */
        dev.isxander.yacl.api.ButtonOption.Builder name(@NotNull Component name);

        /**
         * Sets the tooltip to be used by the option.
         * Can be invoked twice to append more lines.
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltips text lines - merged with a new-line on {@link Option.Builder#build()}.
         */
        dev.isxander.yacl.api.ButtonOption.Builder tooltip(@NotNull Component... tooltips);

        dev.isxander.yacl.api.ButtonOption.Builder action(@NotNull BiConsumer<YACLScreen, ButtonOption> action);

        /**
         * Action to be executed upon button press
         *
         * @see ButtonOption#action()
         */
        @Deprecated
        dev.isxander.yacl.api.ButtonOption.Builder action(@NotNull Consumer<YACLScreen> action);

        /**
         * Sets if the option can be configured
         *
         * @see Option#available()
         */
        dev.isxander.yacl.api.ButtonOption.Builder available(boolean available);

        /**
         * Sets the controller for the option.
         * This is how you interact and change the options.
         *
         * @see dev.isxander.yacl.gui.controllers
         */
        dev.isxander.yacl.api.ButtonOption.Builder controller(@NotNull Function<ButtonOption, Controller<BiConsumer<YACLScreen, ButtonOption>>> control);

        ButtonOption build();
    }
}
