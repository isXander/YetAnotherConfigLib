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
        return new Builder();
    }

    class Builder {
        private Text name;

        private final List<Text> tooltipLines = new ArrayList<>();

        private BiFunction<MinecraftClient, YACLScreen, Screen> screenFunction;

        private Builder() {

        }

        /**
         * Sets name of the category
         *
         * @see ConfigCategory#name()
         */
        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        /**
         * Sets the tooltip to be used by the category.
         * Can be invoked twice to append more lines.
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltips text lines - merged with a new-line on {@link Builder#build()}.
         */
        public Builder tooltip(@NotNull Text... tooltips) {
            Validate.notEmpty(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        /**
         * Screen to open upon selecting this category
         *
         * @see PlaceholderCategory#screen()
         */
        public Builder screen(@NotNull BiFunction<MinecraftClient, YACLScreen, Screen> screenFunction) {
            Validate.notNull(screenFunction, "`screenFunction` cannot be null");

            this.screenFunction = screenFunction;
            return this;
        }

        public PlaceholderCategory build() {
            Validate.notNull(name, "`name` must not be null to build `ConfigCategory`");

            MutableText concatenatedTooltip = Text.empty();
            boolean first = true;
            for (Text line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new PlaceholderCategoryImpl(name, screenFunction, concatenatedTooltip);
        }
    }
}
