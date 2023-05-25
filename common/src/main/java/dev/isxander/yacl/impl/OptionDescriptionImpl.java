package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.OptionDescription;
import dev.isxander.yacl.gui.ImageRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public record OptionDescriptionImpl(Component description, CompletableFuture<Optional<ImageRenderer>> image) implements OptionDescription {
    public static class BuilderImpl implements Builder {
        private final List<Component> descriptionLines = new ArrayList<>();
        private CompletableFuture<Optional<ImageRenderer>> image = CompletableFuture.completedFuture(Optional.empty());
        private boolean imageUnset = true;

        @Override
        public Builder description(Component... description) {
            this.descriptionLines.addAll(Arrays.asList(description));
            return this;
        }

        @Override
        public Builder description(Collection<? extends Component> lines) {
            this.descriptionLines.addAll(lines);
            return this;
        }

        @Override
        public Builder image(ResourceLocation image, int width, int height) {
            Validate.isTrue(imageUnset, "Image already set!");
            Validate.isTrue(width > 0, "Width must be greater than 0!");
            Validate.isTrue(height > 0, "Height must be greater than 0!");

            this.image = ImageRenderer.getOrMakeSync(image, () -> Optional.of(new ImageRenderer.TextureBacked(image, 0, 0, width, height, width, height)));
            imageUnset = false;
            return this;
        }

        @Override
        public Builder image(ResourceLocation image, float u, float v, int width, int height, int textureWidth, int textureHeight) {
            Validate.isTrue(imageUnset, "Image already set!");
            Validate.isTrue(width > 0, "Width must be greater than 0!");
            Validate.isTrue(height > 0, "Height must be greater than 0!");

            this.image = ImageRenderer.getOrMakeSync(image, () -> Optional.of(new ImageRenderer.TextureBacked(image, u, v, width, height, textureWidth, textureHeight)));
            imageUnset = false;
            return this;
        }

        @Override
        public Builder image(Path path, ResourceLocation uniqueLocation) {
            Validate.isTrue(imageUnset, "Image already set!");
            this.image = ImageRenderer.getOrMakeAsync(uniqueLocation, () -> ImageRenderer.NativeImageBacked.createFromPath(path, uniqueLocation));
            imageUnset = false;
            return this;
        }

        @Override
        public Builder gifImage(ResourceLocation image) {
            Validate.isTrue(imageUnset, "Image already set!");
            this.image = ImageRenderer.getOrMakeAsync(image, () -> {
                try {
                    return Optional.of(ImageRenderer.AnimatedNativeImageBacked.createGIFFromTexture(image));
                } catch (IOException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            });
            imageUnset = false;
            return this;
        }

        @Override
        public Builder gifImage(Path path, ResourceLocation uniqueLocation) {
            Validate.isTrue(imageUnset, "Image already set!");
            this.image = ImageRenderer.getOrMakeAsync(uniqueLocation, () -> {
                try {
                    return Optional.of(ImageRenderer.AnimatedNativeImageBacked.createGIF(new FileInputStream(path.toFile()), uniqueLocation));
                } catch (IOException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            });
            imageUnset = false;
            return this;
        }

        @Override
        public Builder webpImage(ResourceLocation image) {
            Validate.isTrue(imageUnset, "Image already set!");
            this.image = ImageRenderer.getOrMakeAsync(image, () -> {
                try {
                    return Optional.of(ImageRenderer.AnimatedNativeImageBacked.createWEBPFromTexture(image));
                } catch (IOException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            });
            imageUnset = false;
            return this;
        }

        @Override
        public Builder webpImage(Path path, ResourceLocation uniqueLocation) {
            Validate.isTrue(imageUnset, "Image already set!");
            this.image = ImageRenderer.getOrMakeAsync(uniqueLocation, () -> {
                try {
                    return Optional.of(ImageRenderer.AnimatedNativeImageBacked.createWEBP(new FileInputStream(path.toFile()), uniqueLocation));
                } catch (IOException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            });
            imageUnset = false;
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
