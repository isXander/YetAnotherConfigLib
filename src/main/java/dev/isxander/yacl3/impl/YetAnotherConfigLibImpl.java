package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.PlaceholderCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.Internal
public final class YetAnotherConfigLibImpl implements YetAnotherConfigLib {
    private final Component title;
    private final ImmutableList<ConfigCategory> categories;
    private final Runnable saveFunction;
    private final Consumer<YACLScreen> initConsumer;

    private boolean generated = false;

    public YetAnotherConfigLibImpl(Component title, ImmutableList<ConfigCategory> categories, Runnable saveFunction, Consumer<YACLScreen> initConsumer) {
        this.title = title;
        this.categories = categories;
        this.saveFunction = saveFunction;
        this.initConsumer = initConsumer;
    }

    @Override
    public Screen generateScreen(Screen parent) {
        if (generated)
            throw new UnsupportedOperationException("To prevent memory leaks, you should only generate a Screen once per instance. Please re-build the instance to generate another GUI.");

        YACLConstants.LOGGER.info("Generating YACL screen");
        generated = true;
        return new YACLScreen(this, parent);
    }

    @Override
    public Component title() {
        return title;
    }

    @Override
    public ImmutableList<ConfigCategory> categories() {
        return categories;
    }

    @Override
    public Runnable saveFunction() {
        return saveFunction;
    }

    @Override
    public Consumer<YACLScreen> initConsumer() {
        return initConsumer;
    }

    @ApiStatus.Internal
    public static final class BuilderImpl implements Builder {
        private Component title;
        private final List<ConfigCategory> categories = new ArrayList<>();
        private Runnable saveFunction = () -> {};
        private Consumer<YACLScreen> initConsumer = screen -> {};

        @Override
        public Builder title(@NotNull Component title) {
            Validate.notNull(title, "`title` cannot be null");

            this.title = title;
            return this;
        }

        @Override
        public Builder category(@NotNull ConfigCategory category) {
            Validate.notNull(category, "`category` cannot be null");

            this.categories.add(category);
            return this;
        }

        @Override
        public Builder categories(@NotNull Collection<? extends @NotNull ConfigCategory> categories) {
            Validate.notNull(categories, "`categories` cannot be null");

            this.categories.addAll(categories);
            return this;
        }

        @Override
        public Builder save(@NotNull Runnable saveFunction) {
            Validate.notNull(saveFunction, "`saveFunction` cannot be null");

            this.saveFunction = saveFunction;
            return this;
        }

        @Override
        public Builder screenInit(@NotNull Consumer<YACLScreen> initConsumer) {
            Validate.notNull(initConsumer, "`initConsumer` cannot be null");

            this.initConsumer = initConsumer;
            return this;
        }

        @Override
        public YetAnotherConfigLib build() {
            Validate.notNull(title, "`title must not be null to build `YetAnotherConfigLib`");
            Validate.notEmpty(categories, "`categories` must not be empty to build `YetAnotherConfigLib`");
            Validate.isTrue(!categories.stream().allMatch(category -> category instanceof PlaceholderCategory), "At least one regular category is required to build `YetAnotherConfigLib`");

            return new YetAnotherConfigLibImpl(title, ImmutableList.copyOf(categories), saveFunction, initConsumer);
        }
    }
}
