package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.ButtonOptionImpl;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface ButtonOption extends Option<Runnable> {
    Runnable action();

    static Builder createBuilder() {
        return new Builder();
    }

    class Builder {
        private Text name;
        private final List<Text> tooltipLines = new ArrayList<>();
        private Function<ButtonOption, Controller<Runnable>> controlGetter;
        private Runnable action;

        private Builder() {

        }

        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        public Builder tooltip(@NotNull Text... tooltips) {
            Validate.notEmpty(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        public Builder action(@NotNull Runnable action) {
            Validate.notNull(action, "`action` cannot be null");

            this.action = action;
            return this;
        }

        public Builder controller(@NotNull Function<ButtonOption, Controller<Runnable>> control) {
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

            return new ButtonOptionImpl(name, concatenatedTooltip, action, controlGetter);
        }
    }
}
