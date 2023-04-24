package dev.isxander.yacl.test.forge;

import dev.isxander.yacl.test.GuiTest;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("yacl_test")
public class ForgeTest {
    public ForgeTest() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, parent) -> GuiTest.getModConfigScreenFactory(parent)
                )
        );
    }
}
