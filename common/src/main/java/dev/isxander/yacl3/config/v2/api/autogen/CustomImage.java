package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.impl.autogen.EmptyCustomImageFactory;
import dev.isxander.yacl3.gui.image.ImageRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Defines a custom image for an option.
 * Without this annotation, the option factory will look
 * for the resource {@code modid:textures/yacl3/$config_id_path/$fieldName.webp}.
 * WEBP was chosen as the default format because file sizes are greatly reduced,
 * which is important to keep your JAR size down, if you're so bothered.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomImage {
    /**
     * The resource path to the image, a {@link net.minecraft.resources.ResourceLocation}
     * is constructed with the namespace being the modid of the config, and the path being
     * this value.
     * <p>
     * The following file formats are supported:
     * <ul>
     *     <li>{@code .png}</li>
     *     <li>{@code .webp}</li>
     *     <li>{@code .jpg}, {@code .jpeg}</li>
     *     <li>{@code .gif} - <strong>HIGHLY DISCOURAGED DUE TO LARGE FILE SIZE</strong></li>
     * </ul>
     * <p>
     * If left blank, then {@link CustomImage#factory()} is used.
     */
    String value() default "";

    /**
     * The width of the image, in pixels.
     * <strong>This is only required when using a PNG with {@link CustomImage#value()}</strong>
     */
    int width() default 0;

    /**
     * The width of the image, in pixels.
     * <strong>This is only required when using a PNG with {@link CustomImage#value()}</strong>
     */
    int height() default 0;

    /**
     * The factory to create the image with.
     * For the average user, this should not be used as it breaks out of the
     * API-safe environment where things could change at any time, but required
     * when creating anything advanced with the {@link ImageRenderer}.
     * <p>
     * The factory should contain a public, no-args constructor that will be
     * invoked via reflection.
     *
     * @return the class of the factory
     */
    Class<? extends CustomImageFactory<?>> factory() default EmptyCustomImageFactory.class;

    interface CustomImageFactory<T> {
        CompletableFuture<ImageRenderer> createImage(T value, ConfigField<T> field, OptionAccess access);
    }
}
