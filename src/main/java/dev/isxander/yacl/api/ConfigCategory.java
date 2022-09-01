package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.ConfigCategoryImpl;
import dev.isxander.yacl.impl.OptionGroupImpl;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface ConfigCategory {
    @NotNull Text name();

    @NotNull ImmutableList<OptionGroup> groups();

    static Builder createBuilder() {
        return new Builder();
    }

    class Builder {
        private Text name;
        private final List<Option<?>> rootOptions = new ArrayList<>();

        private final List<OptionGroup> groups = new ArrayList<>();

        private Builder() {

        }

        public Builder name(@NotNull Text name) {
            Validate.notNull(name, "`name` cannot be null");

            this.name = name;
            return this;
        }

        public Builder option(@NotNull Option<?> option) {
            Validate.notNull(option, "`option` must not be null");

            this.rootOptions.add(option);
            return this;
        }

        public Builder group(@NotNull OptionGroup group) {
            Validate.notNull(group, "`group` must not be null");

            this.groups.add(group);
            return this;
        }

        public ConfigCategory build() {
            Validate.notNull(name, "`name` must not be null to build `ConfigCategory`");
            Validate.notEmpty(rootOptions, "`at least one option must be added to build `ConfigCategory`");

            List<OptionGroup> combinedGroups = new ArrayList<>();
            combinedGroups.add(new OptionGroupImpl(Text.empty(), ImmutableList.copyOf(rootOptions), true));
            combinedGroups.addAll(groups);

            return new ConfigCategoryImpl(name, ImmutableList.copyOf(combinedGroups));
        }
    }
}
