package dev.isxander.yacl.test;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> YetAnotherConfigLib.createBuilder()
                .title(Text.of("Test Suites"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Suites"))
                        .option(ButtonOption.createBuilder()
                                .name(Text.of("Full Test Suite"))
                                .controller(ActionController::new)
                                .action(screen -> MinecraftClient.getInstance().setScreen(getFullTestSuite(screen)))
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Text.of("Basic Wiki Suite"))
                                .controller(ActionController::new)
                                .action(screen -> MinecraftClient.getInstance().setScreen(getWikiBasic(screen)))
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Text.of("Group Wiki Suite"))
                                .controller(ActionController::new)
                                .action(screen -> MinecraftClient.getInstance().setScreen(getWikiGroups(screen)))
                                .build())
                        .build())
                .build().generateScreen(parent);
    }

    private Screen getFullTestSuite(Screen parent) {
        return ClientEntrypoint.getInstance().getYACL().generateScreen(parent);
    }

    private Screen getWikiBasic(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.of("Mod Name"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("My Category"))
                        .tooltip(Text.of("This displays when you hover over a category button")) // optional
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.of("My Boolean Option"))
                                .tooltip(Text.of("This option displays the basic capabilities of YetAnotherConfigLib")) // optional
                                .binding(
                                        true, // default
                                        () -> TestSettings.INSTANCE.booleanToggle, // getter
                                        newValue -> TestSettings.INSTANCE.booleanToggle = newValue // setter
                                )
                                .controller(BooleanController::new)
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }

    private Screen getWikiGroups(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.of("Mod Name"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("My Category"))
                        .tooltip(Text.of("This displays when you hover over a category button")) // optional
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Option Group"))
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.of("My Boolean Option"))
                                        .tooltip(Text.of("This option displays the basic capabilities of YetAnotherConfigLib")) // optional
                                        .binding(
                                                true, // default
                                                () -> TestSettings.INSTANCE.booleanToggle, // getter
                                                newValue -> TestSettings.INSTANCE.booleanToggle = newValue // setter
                                        )
                                        .controller(BooleanController::new)
                                        .build())
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }

    private ConfigScreenFactory<?> getWikiButton() {
        return (parent) -> YetAnotherConfigLib.createBuilder()
                .title(Text.of("Mod Name"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("My Category"))
                        .tooltip(Text.of("This displays when you hover over a category button")) // optional
                        .option(ButtonOption.createBuilder()
                                .name(Text.of("Pressable Button"))
                                .tooltip(Text.of("This is so easy!")) // optional
                                .action(screen -> {})
                                .controller(ActionController::new)
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }


}
