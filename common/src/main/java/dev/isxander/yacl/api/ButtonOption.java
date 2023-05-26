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
