package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.impl.ConfigCategoryImpl;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface ConfigCategory {
    @NotNull Text name();

    @NotNull ImmutableList<Option<?>> options();

    static Builder createBuilder() {
        return new Builder();
    }

    class Builder {
        private Text name;
        private final List<Option<?>> options = new ArrayList<>();

        private Builder() {

        }

        public Builder setName(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        public Builder addOption(@NotNull Option<?> option) {
            Validate.notNull(option, "`option` must not be null");

            this.options.add(option);
            return this;
        }

        public ConfigCategory build() {
            Validate.notNull(name, "`name` must not be null to build `ConfigCategory`");
            Validate.notEmpty(options, "`at least one option must be added to build `ConfigCategory`");

            return new ConfigCategoryImpl(name, ImmutableList.copyOf(options));
        }
    }
}
