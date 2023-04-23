package dev.isxander.yacl.fabric.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl.api.Binding;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public class ModMenuEntrypoint implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return parent -> YetAnotherConfigLib.createBuilder()
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
                    .generateScreen(parent);
        } else {
            return null;
        }
    }
}
