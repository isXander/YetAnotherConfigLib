package dev.isxander.yacl.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.config.ConfigInstance;
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
import java.util.function.BiFunction;
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
        return new YetAnotherConfigLibImpl.BuilderImpl();
    }

    /**
     * Creates an instance using a {@link ConfigInstance} which autofills the save() builder method.
     * This also takes an easy functional interface that provides defaults and config to help build YACL bindings.
     */
    static <T> YetAnotherConfigLib create(ConfigInstance<T> configInstance, ConfigBackedBuilder<T> builder) {
        return builder.build(configInstance.getDefaults(), configInstance.getConfig(), createBuilder().save(configInstance::save)).build();
    }

    interface Builder {
        /**
         * Sets title of GUI for Minecraft narration
         *
         * @see YetAnotherConfigLib#title()
         */
        Builder title(@NotNull Text title);

        /**
         * Adds a new category.
         * To create a category you need to use {@link ConfigCategory#createBuilder()}
         *
         * @see YetAnotherConfigLib#categories()
         */
        Builder category(@NotNull ConfigCategory category);

        /**
         * Adds multiple categories at once.
         * To create a category you need to use {@link ConfigCategory#createBuilder()}
         *
         * @see YetAnotherConfigLib#categories()
         */
        Builder categories(@NotNull Collection<? extends ConfigCategory> categories);

        /**
         * Used to define a save function for when user clicks the Save Changes button
         *
         * @see YetAnotherConfigLib#saveFunction()
         */
        Builder save(@NotNull Runnable saveFunction);

        /**
         * Defines a consumer that is accepted every time the YACL screen initialises
         *
         * @see YetAnotherConfigLib#initConsumer()
         */
        Builder screenInit(@NotNull Consumer<YACLScreen> initConsumer);

        YetAnotherConfigLib build();
    }

    @FunctionalInterface
    interface ConfigBackedBuilder<T> {
        YetAnotherConfigLib.Builder build(T defaults, T config, YetAnotherConfigLib.Builder builder);
    }
}
