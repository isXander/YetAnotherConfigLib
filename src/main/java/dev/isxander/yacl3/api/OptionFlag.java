package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.RequireRestartScreen;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

/**
 * Code that is executed upon certain options being applied.
 * Each flag is executed only once per save, no matter the amount of options with the flag.
 */
@FunctionalInterface
public interface OptionFlag extends Consumer<Minecraft> {
    /** Warns the user that a game restart is required for the changes to take effect */
    OptionFlag GAME_RESTART = client -> GuiUtils.setScreen(new RequireRestartScreen(GuiUtils.getCurrentScreen()));

    /** Reloads chunks upon applying (F3+A) */
    OptionFlag RELOAD_CHUNKS =
            //? if >=26.2 {
            client -> client.levelExtractor.allChanged();
            //? } else {
            /*client -> client.levelRenderer.allChanged();
            *///?}

    @Deprecated
    OptionFlag WORLD_RENDER_UPDATE =
            //? if >=26.2 {
            RELOAD_CHUNKS;
            //?} else {
            /*client -> client.levelRenderer.needsUpdate();
            *///?}

    OptionFlag ASSET_RELOAD = Minecraft::delayTextureReload;
}
