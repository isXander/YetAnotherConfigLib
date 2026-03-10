package dev.isxander.yacl3.gui.image;

import dev.isxander.yacl3.impl.utils.YACLConstants;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class YACLImageReloadListener
        implements PreparableReloadListener
        /*? if fabric {*/,
        net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener
        /*?}*/
{
    @Override
    public @NotNull CompletableFuture<Void> reload(
            SharedState sharedState,
            Executor backgroundExecutor,
            PreparationBarrier preparationBarrier,
            Executor gameExecutor
    ) {
        return this.reload0(sharedState.resourceManager(), backgroundExecutor, preparationBarrier, gameExecutor);
    }

    private @NotNull CompletableFuture<Void> reload0(
            ResourceManager resourceManager,
            Executor backgroundExecutor,
            PreparationBarrier preparationBarrier,
            Executor gameExecutor
    ) {
        return prepare(resourceManager, backgroundExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenCompose(suppliers -> apply(suppliers, gameExecutor));
    }

    private CompletableFuture<List<Optional<SupplierPreparation>>> prepare(
            ResourceManager manager,
            Executor executor
    ) {
        Map<Identifier, Resource> imageResources = manager.listResources(
                "textures",
                location -> ImageRendererManager.PRELOADED_IMAGE_FACTORIES
                        .stream()
                        .anyMatch(factory -> factory.predicate().test(location))
        );

        return imageResources.keySet().stream()
                .map(location -> {
                    ImageRendererFactory imageFactory = ImageRendererManager.PRELOADED_IMAGE_FACTORIES
                            .stream()
                            .filter(factory -> factory.predicate().test(location))
                            .map(factory -> factory.factory().apply(location))
                            .findAny()
                            .orElseThrow();

                    return CompletableFuture.supplyAsync(
                            () -> ImageRendererManager.safelyPrepareFactory(
                                    location, imageFactory
                            ).map(supplier -> new SupplierPreparation(location, supplier)),
                            executor
                    );
                })
                .collect(CompletableFutureCollector.allOf());
    }

    private CompletableFuture<Void> apply(
            List<Optional<SupplierPreparation>> suppliers,
            Executor executor
    ) {
        return CompletableFuture.allOf(suppliers.stream()
                .flatMap(Optional::stream)
                .map(prep -> CompletableFuture.supplyAsync(
                        () -> {
                            ImageRenderer imageRenderer;
                            try {
                                imageRenderer = prep.supplier().completeImage();
                            } catch (Exception e) {
                                YACLConstants.LOGGER.error("Failed to create image '{}'", prep.location(), e);
                                return Optional.empty();
                            }
                            ImageRendererManager.PRELOADED_IMAGE_CACHE.put(prep.location(), imageRenderer);
                            YACLConstants.LOGGER.info("Successfully loaded image '{}'", prep.location());
                            return Optional.of(imageRenderer);
                        },
                        executor
                ))
                .toArray(CompletableFuture<?>[]::new));
    }

    private record SupplierPreparation(Identifier location, ImageRendererFactory.ImageSupplier supplier) {
    }

    public Identifier getId() {
        return YACLPlatform.rl("image_reload_listener");
    }

    /*? if fabric {*/
    @Override
    public Identifier getFabricId() {
        return this.getId();
    }
    /*?}*/

    public static class CompletableFutureCollector<X, T extends CompletableFuture<X>> implements Collector<T, List<T>, CompletableFuture<List<X>>> {
        private CompletableFutureCollector() {
        }

        public static <X, T extends CompletableFuture<X>> Collector<T, List<T>, CompletableFuture<List<X>>> allOf() {
            return new CompletableFutureCollector<>();
        }

        @Override
        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<T>, T> accumulator() {
            return List::add;
        }

        @Override
        public BinaryOperator<List<T>> combiner() {
            return (left, right) -> {
                left.addAll(right);
                return left;
            };
        }

        @Override
        public Function<List<T>, CompletableFuture<List<X>>> finisher() {
            return ls -> CompletableFuture.allOf(ls.toArray(CompletableFuture[]::new))
                    .thenApply(v -> ls
                            .stream()
                            .map(CompletableFuture::join)
                            .toList());
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }
}
