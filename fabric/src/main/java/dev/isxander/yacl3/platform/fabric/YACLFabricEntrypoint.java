package dev.isxander.yacl3.platform.fabric;

import dev.isxander.yacl3.platform.fabric.image.YACLImageReloadListenerFabric;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class YACLFabricEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new YACLImageReloadListenerFabric());
    }
}
