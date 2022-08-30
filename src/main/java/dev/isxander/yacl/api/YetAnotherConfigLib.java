package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.impl.YetAnotherConfigLibImpl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface YetAnotherConfigLib {

    Text title();

    ImmutableList<ConfigCategory> categories();

    Screen generateScreen();

    static Builder createBuilder(Text title) {
        return new Builder(title);
    }

    class Builder {
        private Text title;
        private final List<ConfigCategory> categories = new ArrayList<>();

        private Builder(@NotNull Text title) {
            Validate.notNull(title, "`title` cannot be null");
            this.title = title;
        }

        public Builder setTitle(@NotNull Text title) {
            Validate.notNull(title, "`title` cannot be null");

            this.title = title;
            return this;
        }

        public Builder addCategory(@NotNull ConfigCategory category) {
            Validate.notNull(category, "`category` cannot be null");

            this.categories.add(category);
            return this;
        }

        public YetAnotherConfigLib build() {
            Validate.notNull(title, "`title must not be null to build `YetAnotherConfigLib`");
            Validate.notEmpty(categories, "`categories` must not be empty to build `YetAnotherConfigLib`");

            return new YetAnotherConfigLibImpl(title, ImmutableList.copyOf(categories));
        }
    }
}
