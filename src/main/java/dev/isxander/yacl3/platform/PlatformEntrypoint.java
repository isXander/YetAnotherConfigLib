package dev.isxander.yacl3.platform;

import dev.isxander.yacl3.gui.image.YACLImageReloadListener;

/*? if fabric {*/
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class PlatformEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        YACLConfig.HANDLER.load();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new YACLImageReloadListener());
    }
}
/*?} elif neoforge {*/
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@Mod("yet_another_config_lib_v3")
public class PlatformEntrypoint {
    public PlatformEntrypoint(IEventBus modEventBus) {
        YACLConfig.HANDLER.load();
        modEventBus.addListener(RegisterClientReloadListenersEvent.class, event -> {
            event.registerReloadListener(new YACLImageReloadListener());
        });
    }
}
*//*?} elif forge {*//*
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("yet_another_config_lib_v3")
public class PlatformEntrypoint {
    public PlatformEntrypoint() {
        YACLConfig.HANDLER.load();
        FMLJavaModLoadingContext.get().getModEventBus().<RegisterClientReloadListenersEvent>addListener(event -> {
            event.registerReloadListener(new YACLImageReloadListener());
        });
    }
}
*//*?}*/
