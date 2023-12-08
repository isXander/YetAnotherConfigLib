package dev.isxander.yacl3.impl;

import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.isxander.yacl3.gui.image.ImageRendererManager;
import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import dev.isxander.yacl3.gui.image.impl.DynamicTextureImage;
import dev.isxander.yacl3.gui.image.impl.ResourceTextureImage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public record OptionDescriptionImpl(Component text, CompletableFuture<Optional<ImageRenderer>> image) implements OptionDescription {
    public static class BuilderImpl implements Builder {
        private final List<Component> descriptionLines = new ArrayList<>();
        private CompletableFuture<Optional<ImageRenderer>> image = CompletableFuture.completedFuture(Optional.empty());
        private boolean imageUnset = true;

        @Override
        public Builder text(Component... description) {
            this.descriptionLines.addAll(Arrays.asList(description));
            return this;
        }

        @Override
        public Builder text(Collection<? extends Component> lines) {
            this.descriptionLines.addAll(lines);
            return this;
        }

        @Override
        public Builder image(ResourceLocation image, int width, int height) {
            Validate.isTrue(imageUnset, "Image already set!");
            Validate.isTrue(width > 0, "Width must be greater than 0!");
            Validate.isTrue(height > 0, "Height must be greater than 0!");

            this.image = ImageRendererManager.registerImage(image, ResourceTextureImage.createFactory(image, 0, 0, width, height, width, height)).thenApply(Optional::of);
            imageUnset = false;
            return this;
        }

        @Override
        public Builder image(ResourceLocation image, float u, float v, int width, int height, int textureWidth, int textureHeight) {
            Validate.isTrue(imageUnset, "Image already set!");
            Validate.isTrue(width > 0, "Width must be greater than 0!");
            Validate.isTrue(height > 0, "Height must be greater than 0!");

            this.image = ImageRendererManager.registerImage(image, ResourceTextureImage.createFactory(image, u, v, width, height, textureWidth, textureHeight)).thenApply(Optional::of);
            imageUnset = false;
            return this;
        }

        @Override
        public Builder image(Path path, ResourceLocation uniqueLocation) {
            Validate.isTrue(imageUnset, "Image already set!");

            this.image = ImageRendererManager.registerImage(uniqueLocation, DynamicTextureImage.fromPath(path, uniqueLocation)).thenApply(Optional::of);
            imageUnset = false;
            return this;
        }

        @Override
        public Builder gifImage(ResourceLocation image) {
            Validate.isTrue(imageUnset, "Image already set!");

            this.image = ImageRendererManager.registerImage(image, AnimatedDynamicTextureImage.createGIFFromTexture(image)).thenApply(Optional::of);
            imageUnset = false;
            return this;
        }

        @Override
        public Builder gifImage(Path path, ResourceLocation uniqueLocation) {
            Validate.isTrue(imageUnset, "Image already set!");

            this.image = ImageRendererManager.registerImage(uniqueLocation, AnimatedDynamicTextureImage.createGIFFromPath(path, uniqueLocation)).thenApply(Optional::of);
            imageUnset = false;
            return this;
        }

        @Override
        public Builder webpImage(ResourceLocation image) {
            Validate.isTrue(imageUnset, "Image already set!");

            Optional<ImageRenderer> completedImage = ImageRendererManager.getImage(image);
            if (completedImage.isPresent()) {
                this.image = CompletableFuture.completedFuture(completedImage);
            } else {
                this.image = ImageRendererManager.registerImage(image, AnimatedDynamicTextureImage.createWEBPFromTexture(image)).thenApply(Optional::of);
            }

            imageUnset = false;
            return this;
        }

        @Override
        public Builder webpImage(Path path, ResourceLocation uniqueLocation) {
            Validate.isTrue(imageUnset, "Image already set!");

            this.image = ImageRendererManager.registerImage(uniqueLocation, AnimatedDynamicTextureImage.createWEBPFromPath(path, uniqueLocation)).thenApply(Optional::of);
            imageUnset = false;
            return this;
        }

        @Override
        public Builder customImage(CompletableFuture<Optional<ImageRenderer>> image) {
            Validate.notNull(image, "Image cannot be null!");
            Validate.isTrue(imageUnset, "Image already set!");

            this.image = image;
            this.imageUnset = false;
            return this;
        }

        @Override
        public OptionDescription build() {
            MutableComponent concatenatedDescription = Component.empty();
            Iterator<Component> iter = descriptionLines.iterator();
            while (iter.hasNext()) {
                concatenatedDescription.append(iter.next());
                if (iter.hasNext()) concatenatedDescription.append("\n");
            }

            return new OptionDescriptionImpl(concatenatedDescription, image);
        }
    }
}
