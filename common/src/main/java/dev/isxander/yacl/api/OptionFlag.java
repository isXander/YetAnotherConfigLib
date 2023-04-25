package dev.isxander.yacl.api;

import dev.isxander.yacl.gui.RequireRestartScreen;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

/**
 * Code that is executed upon certain options being applied.
 * Each flag is executed only once per save, no matter the amount of options with the flag.
 */
@FunctionalInterface
public interface OptionFlag extends Consumer<Minecraft> {
    /** Warns the user that a game restart is required for the changes to take effect */
    OptionFlag GAME_RESTART = client -> client.setScreen(new RequireRestartScreen(client.screen));

    /** Reloads chunks upon applying (F3+A) */
    OptionFlag RELOAD_CHUNKS = client -> client.levelRenderer.allChanged();

    OptionFlag WORLD_RENDER_UPDATE = client -> client.levelRenderer.needsUpdate();

    OptionFlag ASSET_RELOAD = Minecraft::delayTextureReload;
}
