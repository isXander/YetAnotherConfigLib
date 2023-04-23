package dev.isxander.yacl.forge;

import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("yet_another_config_lib")
public class YACLForgeEntrypoint {
    public YACLForgeEntrypoint() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Test"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Test"))
                        .option(Option.createBuilder(boolean.class)
                                .name(Component.literal("Test"))
                                .binding(Binding.immutable(true))
                                .controller(TickBoxController::new)
                                .build())
                        .build())
                .build()
                .generateScreen(parent)));
    }

}
