package dev.isxander.yacl.api;

import dev.isxander.yacl.gui.ImageRenderer;
import dev.isxander.yacl.impl.OptionDescriptionImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface OptionDescription {
    Component descriptiveName();

    Component description();

    CompletableFuture<Optional<ImageRenderer>> image();

    static Builder createBuilder() {
        return new OptionDescriptionImpl.BuilderImpl();
    }

    interface Builder {
        Builder name(Component name);

        Builder description(Component description);

        Builder image(ResourceLocation image, int width, int height);
        Builder image(Path path, ResourceLocation uniqueLocation);

        Builder webpImage(ResourceLocation image);
        Builder webpImage(Path path, ResourceLocation uniqueLocation);

        @Deprecated
        Builder gifImage(ResourceLocation image);
        @Deprecated
        Builder gifImage(Path path, ResourceLocation uniqueLocation);

        OptionDescription build();
    }
}
