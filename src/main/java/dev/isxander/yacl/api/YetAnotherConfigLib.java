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
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Main class of the mod.
 * Contains all data and used to provide a {@link Screen}
 */
public interface YetAnotherConfigLib {
    /**
     * Title of the GUI. Only used for Minecraft narration.
     */
    Text title();

    /**
     * Gets all config categories.
     */
    ImmutableList<ConfigCategory> categories();

    /**
     * Ran when changes are saved. Can be used to save config to a file etc.
     */
    Runnable saveFunction();

    /**
     * Ran every time the YACL screen initialises. Can be paired with FAPI to add custom widgets.
     */
    Consumer<YACLScreen> initConsumer();

    /**
     * Generates a Screen to display based on this instance.
     *
     * @param parent parent screen to open once closed
     */
    Screen generateScreen(@Nullable Screen parent);

    /**
     * Creates a builder to construct YACL
     */
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

        /**
         * Sets title of GUI for Minecraft narration
         *
         * @see YetAnotherConfigLib#title()
         */
        public Builder title(@NotNull Text title) {
            Validate.notNull(title, "`title` cannot be null");

            this.title = title;
            return this;
        }

        /**
         * Adds a new category.
         * To create a category you need to use {@link ConfigCategory#createBuilder()}
         *
         * @see YetAnotherConfigLib#categories()
         */
        public Builder category(@NotNull ConfigCategory category) {
            Validate.notNull(category, "`category` cannot be null");

            this.categories.add(category);
            return this;
        }

        /**
         * Adds multiple categories at once.
         * To create a category you need to use {@link ConfigCategory#createBuilder()}
         *
         * @see YetAnotherConfigLib#categories()
         */
        public Builder categories(@NotNull Collection<ConfigCategory> categories) {
            Validate.notEmpty(categories, "`categories` cannot be empty");

            this.categories.addAll(categories);
            return this;
        }

        /**
         * Used to define a save function for when user clicks the Save Changes button
         *
         * @see YetAnotherConfigLib#saveFunction()
         */
        public Builder save(@NotNull Runnable saveFunction) {
            Validate.notNull(saveFunction, "`saveFunction` cannot be null");

            this.saveFunction = saveFunction;
            return this;
        }

        /**
         * Defines a consumer that is accepted every time the YACL screen initialises
         *
         * @see YetAnotherConfigLib#initConsumer()
         */
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
