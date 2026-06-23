package dev.isxander.yacl3.platform;

import dev.isxander.yacl3.gui.image.YACLImageReloadListener;

/*? if fabric {*/
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;

public class PlatformEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        YACLConfig.HANDLER.load();
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(YACLImageReloadListener.getId(), new YACLImageReloadListener());
    }
}
/*?} elif neoforge {*/
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;

@Mod("yet_another_config_lib_v3")
public class PlatformEntrypoint {
    public PlatformEntrypoint(IEventBus modEventBus) {
        YACLConfig.HANDLER.load();
        modEventBus.addListener(AddClientReloadListenersEvent.class, event -> {
            event.addListener(YACLImageReloadListener.getId(), new YACLImageReloadListener());
        });
    }
}
*//*?} elif forge {*/
/*import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("yet_another_config_lib_v3")
public class PlatformEntrypoint {
    public PlatformEntrypoint() {
        YACLConfig.HANDLER.load();
        // noinspection removal we will never support lexforge past 1.20.1
        FMLJavaModLoadingContext.get().getModEventBus().<RegisterClientReloadListenersEvent>addListener(event -> {
            event.registerReloadListener(new YACLImageReloadListener());
        });
    }
}
*//*?}*/
