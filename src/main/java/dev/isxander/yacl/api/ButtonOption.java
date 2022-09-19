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
        return new Builder();
    }

    class Builder {
        private Text name;
        private final List<Text> tooltipLines = new ArrayList<>();
        private boolean available = true;
        private Function<ButtonOption, Controller<BiConsumer<YACLScreen, ButtonOption>>> controlGetter;
        private BiConsumer<YACLScreen, ButtonOption> action;

        private Builder() {

        }

        /**
         * Sets the name to be used by the option.
         *
         * @see Option#name()
         */
        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        /**
         * Sets the tooltip to be used by the option.
         * Can be invoked twice to append more lines.
         * No need to wrap the text yourself, the gui does this itself.
         *
         * @param tooltips text lines - merged with a new-line on {@link Option.Builder#build()}.
         */
        public Builder tooltip(@NotNull Text... tooltips) {
            Validate.notNull(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        public Builder action(@NotNull BiConsumer<YACLScreen, ButtonOption> action) {
            Validate.notNull(action, "`action` cannot be null");

            this.action = action;
            return this;
        }

        /**
         * Action to be executed upon button press
         *
         * @see ButtonOption#action()
         */
        @Deprecated
        public Builder action(@NotNull Consumer<YACLScreen> action) {
            Validate.notNull(action, "`action` cannot be null");

            this.action = (screen, button) -> action.accept(screen);
            return this;
        }

        /**
         * Sets if the option can be configured
         *
         * @see Option#available()
         */
        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        /**
         * Sets the controller for the option.
         * This is how you interact and change the options.
         *
         * @see dev.isxander.yacl.gui.controllers
         */
        public Builder controller(@NotNull Function<ButtonOption, Controller<BiConsumer<YACLScreen, ButtonOption>>> control) {
            Validate.notNull(control, "`control` cannot be null");

            this.controlGetter = control;
            return this;
        }

        public ButtonOption build() {
            Validate.notNull(name, "`name` must not be null when building `Option`");
            Validate.notNull(controlGetter, "`control` must not be null when building `Option`");
            Validate.notNull(action, "`action` must not be null when building `Option`");

            MutableText concatenatedTooltip = Text.empty();
            boolean first = true;
            for (Text line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new ButtonOptionImpl(name, concatenatedTooltip, action, available, controlGetter);
        }
    }
}
