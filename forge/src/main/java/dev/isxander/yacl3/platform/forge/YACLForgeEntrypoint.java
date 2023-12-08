package dev.isxander.yacl3.platform.forge;

import net.minecraftforge.fml.common.Mod;

@Mod("yet_another_config_lib_v3")
public class YACLForgeEntrypoint {
    public YACLForgeEntrypoint(IEventBus modEventBus) {
        modEventBus.addListener(RegisterClientReloadListenersEvent.class, event -> {
            System.out.println("image reload event");
            event.registerReloadListener(new YACLImageReloadListener());
        });
    }
}
