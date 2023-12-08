package dev.isxander.yacl3.platform.fabric.image;

import dev.isxander.yacl3.gui.image.YACLImageReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

public class YACLImageReloadListenerFabric extends YACLImageReloadListener implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation("yet_another_config_lib_v3", "image_reload_listener");
    }
}
