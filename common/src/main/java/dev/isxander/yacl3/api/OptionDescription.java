package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.ImageRenderer;
import dev.isxander.yacl3.impl.OptionDescriptionImpl;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Provides all information for the description panel in the GUI.
 * This provides no functional benefit, and is purely for UX.
 */
public interface OptionDescription {
    /**
     * The description of the option, this is automatically wrapped and supports all styling,
     * including {@link net.minecraft.network.chat.ClickEvent}s and {@link net.minecraft.network.chat.HoverEvent}s.
     * @return The description of the option, with all lines merged with \n.
     */
    Component text();

    /**
     * The image to display with the description. If the Optional is empty, no image has been provided.
     * Usually, the image renderers are constructed asynchronously, so this method returns a {@link CompletableFuture}.
     * <p>
     * Image renderers are cached throughout the whole lifecycle of the game, and should not be generated more than once
     * per image. See {@link ImageRenderer#getOrMakeAsync(ResourceLocation, Supplier)} for implementation details.
     */
    CompletableFuture<Optional<ImageRenderer>> image();

    /**
     * @return a new builder for an {@link OptionDescription}.
     */
    static Builder createBuilder() {
        return new OptionDescriptionImpl.BuilderImpl();
    }

    static OptionDescription of(Component... description) {
        return createBuilder().text(description).build();
    }

    OptionDescription EMPTY = new OptionDescriptionImpl(CommonComponents.EMPTY, CompletableFuture.completedFuture(Optional.empty()));

    interface Builder {
        /**
         * Appends lines to the main description of the option. This can be called multiple times.
         * On {@link Builder#build()}, the lines are merged with \n.
         * @see OptionDescription#text()
         *
         * @param description the lines to append to the description.
         * @return this builder
         */
        Builder text(Component... description);

        /**
         * Appends lines to the main description of the option. This can be called multiple times.
         * On {@link Builder#build()}, the lines are merged with \n.
         * @see OptionDescription#text()
         *
         * @param lines the lines to append to the description.
         * @return this builder
         */
        Builder text(Collection<? extends Component> lines);

        /**
         * Sets a static image to display with the description. This is backed by a regular minecraft resource
         * in your mod's /assets folder.
         *
         * @param image the location of the image to display from the resource manager
         * @param width the width of the texture
         * @param height the height of the texture
         * @return this builder
         */
        Builder image(ResourceLocation image, int width, int height);

        /**
         * Sets a static image to display with the description. This is backed by a regular minecraft resource
         * in your mod's /assets folder. This overload method allows you to specify a subsection of the texture to render.
         *
         * @param image the location of the image to display from the resource manager
         * @param u the u coordinate
         * @param v the v coordinate
         * @param width the width of the subsection
         * @param height the height of the subsection
         * @param textureWidth the width of the whole texture file
         * @param textureHeight the height of whole texture file
         * @return this builder
         */
        Builder image(ResourceLocation image, float u, float v, int width, int height, int textureWidth, int textureHeight);

        /**
         * Sets a static image to display with the description. This is backed by a file on disk.
         * The width and height is automatically determined from the image processing.
         *
         * @param path the absolute path to the image file
         * @param uniqueLocation the unique identifier for the image, used for caching and resource manager registrar
         * @return this builder
         */
        Builder image(Path path, ResourceLocation uniqueLocation);

        /**
         * Sets a static OR ANIMATED webP image to display with the description. This is backed by a regular minecraft resource
         * in your mod's /assets folder.
         *
         * @param image the location of the image to display from the resource manager
         * @return this builder
         */
        Builder webpImage(ResourceLocation image);

        /**
         * Sets a static OR ANIMATED webP image to display with the description. This is backed by a file on disk.
         * The width and height is automatically determined from the image processing.
         *
         * @param path the absolute path to the image file
         * @param uniqueLocation the unique identifier for the image, used for caching and resource manager registrar
         * @return this builder
         */
        Builder webpImage(Path path, ResourceLocation uniqueLocation);

        /**
         * Sets a custom image renderer to display with the description.
         * This is useful for rendering other abstract things relevant to your mod.
         * <p>
         * However, <strong>THIS IS NOT API SAFE!</strong> As part of the gui package, things
         * may change that could break compatibility with future versions of YACL.
         * A helpful utility (that is also not API safe) is {@link ImageRenderer#getOrMakeAsync(ResourceLocation, Supplier)}
         * which will cache the image renderer for the whole game lifecycle and construct it asynchronously to the render thread.
         * @param image the image renderer to display
         * @return this builder
         */
        Builder customImage(CompletableFuture<Optional<ImageRenderer>> image);

        /**
         * Sets an animated GIF image to display with the description. This is backed by a regular minecraft resource
         * in your mod's /assets folder.
         *
         * @param image the location of the image to display from the resource manager
         * @return this builder
         */
        @Deprecated
        Builder gifImage(ResourceLocation image);

        /**
         * Sets an animated GIF image to display with the description. This is backed by a file on disk.
         * The width and height is automatically determined from the image processing.
         *
         * @param path the absolute path to the image file
         * @param uniqueLocation the unique identifier for the image, used for caching and resource manager registrar
         * @return this builder
         */
        @Deprecated
        Builder gifImage(Path path, ResourceLocation uniqueLocation);

        OptionDescription build();
    }
}
