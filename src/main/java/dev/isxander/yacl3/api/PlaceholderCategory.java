package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.PlaceholderCategoryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

/**
 * A placeholder category that actually just opens another screen,
 * instead of displaying options.
 * <p>
 * Use of this is discouraged, as it is not very user-friendly and navigating to a placeholder
 * tab that opens another screen is not very intuitive, making keyboard navigation impossible.
 */
public interface PlaceholderCategory extends ConfigCategory {
    /**
     * Function to create a screen to open upon changing to this category
     */
    BiFunction<Minecraft, YACLScreen, Screen> screen();

    static Builder createBuilder() {
        return new PlaceholderCategoryImpl.BuilderImpl();
    }

    interface Builder {
        /**
         * Sets name of the category
         *
         * @see ConfigCategory#name()
         */
        Builder name(@NotNull Component name);

        /**
         * Sets the tooltip to be used by the category.
         * Can be invoked twice to append more lines.
         * No need to wrap the Component yourself, the gui does this itself.
         *
         * @param tooltips Component lines - merged with a new-line on {@link dev.isxander.yacl3.api.PlaceholderCategory.Builder#build()}.
         */
        Builder tooltip(@NotNull Component... tooltips);

        /**
         * Screen to open upon selecting this category
         *
         * @see PlaceholderCategory#screen()
         */
        Builder screen(@NotNull BiFunction<Minecraft, YACLScreen, Screen> screenFunction);

        PlaceholderCategory build();
    }
}
