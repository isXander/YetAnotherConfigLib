package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.impl.OptionGroupImpl;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface OptionGroup {
    Text name();

    @NotNull ImmutableList<Option<?>> options();

    boolean isRoot();

    static Builder createBuilder() {
        return new Builder();
    }

    class Builder {
        private Text name = Text.empty();
        private final List<Option<?>> options = new ArrayList<>();

        private Builder() {

        }

        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` must not be null");

            this.name = name;
            return this;
        }

        public Builder option(@NotNull Option<?> option) {
            Validate.notNull(option, "`option` must not be null");

            this.options.add(option);
            return this;
        }

        public OptionGroup build() {
            Validate.notEmpty(options, "`options` must not be empty to build `OptionGroup`");

            return new OptionGroupImpl(name, ImmutableList.copyOf(options), false);
        }
    }
}
