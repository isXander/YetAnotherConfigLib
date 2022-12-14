package dev.isxander.yacl.api;

import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.PlaceholderCategoryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A placeholder category that actually just opens another screen,
 * instead of displaying options
 */
public interface PlaceholderCategory extends ConfigCategory {
    /**
     * Function to create a screen to open upon changing to this category
     */
    BiFunction<MinecraftClient, YACLScreen, Screen> screen();

    static Builder createBuilder() {
        return new PlaceholderCategoryImpl.BuilderImpl();
    }

    interface Builder {
        /**
         * Sets name of the category
         *
         * @see ConfigCategory#name()
         */
        Builder name(@NotNull Text name);

        /**
         * Sets the tooltip to be used by the category.
         * Can be invoked twice to append more lines.
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltips text lines - merged with a new-line on {@link Builder#build()}.
         */
        Builder tooltip(@NotNull Text... tooltips);

        /**
         * Screen to open upon selecting this category
         *
         * @see PlaceholderCategory#screen()
         */
        Builder screen(@NotNull BiFunction<MinecraftClient, YACLScreen, Screen> screenFunction);

        PlaceholderCategory build();
    }
}
