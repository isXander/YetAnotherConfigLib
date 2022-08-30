package dev.isxander.yacl.api;

import dev.isxander.yacl.impl.OptionImpl;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Option<T> {
    @NotNull Text name();

    @Nullable Text tooltip();

    @NotNull Control<T> control();

    @NotNull Binding<T> binding();

    boolean changed();

    void requestSet(T value);

    void applyValue();

    static <T> Builder<T> createBuilder() {
        return new Builder<>();
    }

    class Builder<T> {
        private Text name;

        private final List<Text> tooltipLines = new ArrayList<>();

        private Control<T> control;

        private Binding<T> binding;

        private Builder() {

        }

        public Builder<T> name(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        public Builder<T> tooltip(@NotNull Text... tooltips) {
            Validate.notEmpty(tooltips, "`tooltips` cannot be empty");

            tooltipLines.addAll(List.of(tooltips));
            return this;
        }

        public Builder<T> controller(@NotNull Control<T> control) {
            Validate.notNull(control, "`control` cannot be null");

            this.control = control;
            return this;
        }

        public Builder<T> binding(@NotNull Binding<T> binding) {
            Validate.notNull(binding, "`binding` cannot be null");

            this.binding = binding;
            return this;
        }

        public Builder<T> binding(@NotNull T def, @NotNull Supplier<@NotNull T> getter, @NotNull Consumer<@NotNull T> setter) {
            Validate.notNull(def, "`default` must not be null");
            Validate.notNull(getter, "`getter` must not be null");
            Validate.notNull(setter, "`setter` must not be null");

            this.binding = Binding.of(def, getter, setter);
            return this;
        }

        public Option<T> build() {
            Validate.notNull(name, "`name` must not be null when building `Option`");
            Validate.notNull(control, "`control` must not be null when building `Option`");
            Validate.notNull(binding, "`binding` must not be null when building `Option`");

            MutableText concatenatedTooltip = Text.empty();
            boolean first = true;
            for (Text line : tooltipLines) {
                if (!first) concatenatedTooltip.append("\n");
                first = false;

                concatenatedTooltip.append(line);
            }

            return new OptionImpl<>(name, concatenatedTooltip, control, binding);
        }
    }
}
