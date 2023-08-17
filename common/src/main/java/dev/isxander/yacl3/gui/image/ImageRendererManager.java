package dev.isxander.yacl3.gui.image;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class ImageRendererManager {
    private static final ExecutorService SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor(task -> new Thread(task, "YACL Image Prep"));

    private static final Map<ResourceLocation, CompletableFuture<ImageRenderer>> IMAGE_CACHE = new ConcurrentHashMap<>();
    private static final Queue<FactoryIDPair<?>> CREATION_QUEUE = new ConcurrentLinkedQueue<>();

    public static <T extends ImageRenderer> CompletableFuture<T> registerImage(ResourceLocation id, ImageRendererFactory<T> factory) {
        SINGLE_THREAD_EXECUTOR.submit(() -> {
            try {
                ImageRendererFactory.ImageSupplier<T> supplier = factory.prepareImage();
                CREATION_QUEUE.add(new FactoryIDPair<>(id, supplier));
            } catch (Exception e) {
                YACLConstants.LOGGER.error("Failed to prepare image '{}'", id, e);
                IMAGE_CACHE.remove(id);
            }
        });

        var future = new CompletableFuture<ImageRenderer>();
        IMAGE_CACHE.put(id, future);
        return (CompletableFuture<T>) future;
    }

    public static void pollImageFactories() {
        RenderSystem.assertOnRenderThread();

        while (!CREATION_QUEUE.isEmpty()) {
            FactoryIDPair<?> pair = CREATION_QUEUE.poll();

            // sanity check - this should never happen
            if (!IMAGE_CACHE.containsKey(pair.id())) {
                YACLConstants.LOGGER.error("Tried to finalise image '{}' but it was not found in cache.", pair.id());
                continue;
            }

            ImageRenderer image;
            try {
                image = pair.supplier().completeImage();
            } catch (Exception e) {
                YACLConstants.LOGGER.error("Failed to create image '{}'", pair.id(), e);
                continue;
            }

            CompletableFuture<ImageRenderer> future = IMAGE_CACHE.get(pair.id());
            // another sanity check - this should never happen
            if (future.isDone()) {
                YACLConstants.LOGGER.error("Image '{}' was already completed", pair.id());
                continue;
            }

            future.complete(image);
        }
    }

    public static void closeAll() {
        SINGLE_THREAD_EXECUTOR.shutdownNow();
        CREATION_QUEUE.clear();
        IMAGE_CACHE.values().forEach(future -> {
            if (future.isDone()) {
                future.join().close();
            }
        });
        IMAGE_CACHE.clear();
    }

    private record FactoryIDPair<T extends ImageRenderer>(ResourceLocation id, ImageRendererFactory.ImageSupplier<T> supplier) {}
}
