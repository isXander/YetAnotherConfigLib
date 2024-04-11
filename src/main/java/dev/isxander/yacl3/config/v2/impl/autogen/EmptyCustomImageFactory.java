package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.CustomImage;
import dev.isxander.yacl3.gui.image.ImageRenderer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EmptyCustomImageFactory implements CustomImage.CustomImageFactory<Object> {

    @Override
    public CompletableFuture<ImageRenderer> createImage(Object value, ConfigField<Object> field, OptionAccess access) {
        throw new IllegalStateException();
    }
}
