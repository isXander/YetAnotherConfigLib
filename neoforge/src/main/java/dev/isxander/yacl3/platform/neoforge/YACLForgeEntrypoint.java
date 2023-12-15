package dev.isxander.yacl3.platform.neoforge;

import dev.isxander.yacl3.gui.image.YACLImageReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod("yet_another_config_lib_v3")
public class YACLForgeEntrypoint {
    public YACLForgeEntrypoint(IEventBus modEventBus) {
        modEventBus.addListener(RegisterClientReloadListenersEvent.class, event -> {
            System.out.println("image reload event");
            event.registerReloadListener(new YACLImageReloadListener());
        });
    }

}
