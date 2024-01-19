package dev.isxander.yacl3.gui.image;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ImageRendererManager {
    private static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor(task -> new Thread(task, "YACL Image Prep"));

    private static final Map<ResourceLocation, CompletableFuture<ImageRenderer>> IMAGE_CACHE = new ConcurrentHashMap<>();
    static final Map<ResourceLocation, ImageRenderer> PRELOADED_IMAGE_CACHE = new ConcurrentHashMap<>();

    static final List<PreloadedImageFactory> PRELOADED_IMAGE_FACTORIES = List.of(
            new PreloadedImageFactory(
                    location -> location.getPath().endsWith(".webp"),
                    AnimatedDynamicTextureImage::createWEBPFromResource
            ),
            new PreloadedImageFactory(
                    location -> location.getPath().endsWith(".gif"),
                    AnimatedDynamicTextureImage::createGIFFromResource
            )
    );

    public static <T extends ImageRenderer> Optional<T> getImage(ResourceLocation id) {
        if (PRELOADED_IMAGE_CACHE.containsKey(id)) {
            return Optional.of((T) PRELOADED_IMAGE_CACHE.get(id));
        }

        if (IMAGE_CACHE.containsKey(id)) {
            // warn developers if they don't put their webp/gif images `/textures` folder
            if (YACLPlatform.isDevelopmentEnv()) {
                if (PRELOADED_IMAGE_FACTORIES.stream().anyMatch(factory -> factory.predicate().test(id))) {
                    YACLConstants.LOGGER.error("Image '{}' not preloaded. MAKE SURE THAT ALL YACL WEBP/GIF IMAGES ARE INSIDE YOUR ASSETS `/textures` FOLDER, ELSE THEY WILL NOT BE PRELOADED!!! THIS ERROR WILL NOT APPEAR IN PROD", id);
                }
            }

            return Optional.ofNullable((T) IMAGE_CACHE.get(id).getNow(null));
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static <T extends ImageRenderer> CompletableFuture<T> registerImage(ResourceLocation id, ImageRendererFactory factory) {
        if (IMAGE_CACHE.containsKey(id)) {
            return (CompletableFuture<T>) IMAGE_CACHE.get(id);
        }

        var future = new CompletableFuture<ImageRenderer>();
        IMAGE_CACHE.put(id, future);

        SINGLE_THREAD_EXECUTOR.submit(() -> {
            Supplier<Optional<ImageRendererFactory.ImageSupplier>> supplier =
                    factory.requiresOffThreadPreparation()
                            ? new CompletedSupplier<>(safelyPrepareFactory(id, factory))
                            : () -> safelyPrepareFactory(id, factory);

            Minecraft.getInstance().execute(() -> completeImageFactory(id, supplier, future));
        });

        return (CompletableFuture<T>) future;
    }

    private static void completeImageFactory(ResourceLocation id, Supplier<Optional<ImageRendererFactory.ImageSupplier>> supplier, CompletableFuture<ImageRenderer> future) {
        RenderSystem.assertOnRenderThread();

        ImageRendererFactory.ImageSupplier completableImage = supplier.get().orElse(null);
        if (completableImage == null) {
            return;
        }

        // sanity check - this should never happen
        if (future.isDone()) {
            YACLConstants.LOGGER.error("Image '{}' was already completed", id);
            return;
        }

        ImageRenderer image;
        try {
            image = completableImage.completeImage();
        } catch (Exception e) {
            YACLConstants.LOGGER.error("Failed to create image '{}'", id, e);
            return;
        }

        future.complete(image);
    }

    public static void closeAll() {
        SINGLE_THREAD_EXECUTOR.shutdownNow();
        IMAGE_CACHE.values().removeIf(future -> {
            if (future.isDone()) {
                future.join().close();
            }
            return true;
        });
    }

    static Optional<ImageRendererFactory.ImageSupplier> safelyPrepareFactory(ResourceLocation id, ImageRendererFactory factory) {
        try {
            return Optional.of(factory.prepareImage());
        } catch (Exception e) {
            YACLConstants.LOGGER.error("Failed to prepare image '{}'", id, e);
            IMAGE_CACHE.remove(id);
            return Optional.empty();
        }
    }

    public record PreloadedImageFactory(Predicate<ResourceLocation> predicate, BiFunction<Resource, ResourceLocation, ImageRendererFactory> factory) {
    }

    private record CompletedSupplier<T>(T get) implements Supplier<T> {
    }

}
