package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.YetAnotherConfigLibImpl;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface YetAnotherConfigLib {

    Text title();

    ImmutableList<ConfigCategory> categories();

    Runnable saveFunction();

    Consumer<YACLScreen> initConsumer();

    Screen generateScreen(@Nullable Screen parent);

    static Builder createBuilder() {
        return new Builder();
    }

    class Builder {
        private Text title;
        private final List<ConfigCategory> categories = new ArrayList<>();
        private Runnable saveFunction = () -> {};
        private Consumer<YACLScreen> initConsumer = screen -> {};

        private Builder() {

        }

        public Builder title(@NotNull Text title) {
            Validate.notNull(title, "`title` cannot be null");

            this.title = title;
            return this;
        }

        public Builder category(@NotNull ConfigCategory category) {
            Validate.notNull(category, "`category` cannot be null");

            this.categories.add(category);
            return this;
        }

        public Builder save(@NotNull Runnable saveFunction) {
            Validate.notNull(saveFunction, "`saveFunction` cannot be null");

            this.saveFunction = saveFunction;
            return this;
        }

        public Builder screenInit(@NotNull Consumer<YACLScreen> initConsumer) {
            Validate.notNull(initConsumer, "`initConsumer` cannot be null");

            this.initConsumer = initConsumer;
            return this;
        }

        public YetAnotherConfigLib build() {
            Validate.notNull(title, "`title must not be null to build `YetAnotherConfigLib`");
            Validate.notEmpty(categories, "`categories` must not be empty to build `YetAnotherConfigLib`");

            return new YetAnotherConfigLibImpl(title, ImmutableList.copyOf(categories), saveFunction, initConsumer);
        }
    }
}
