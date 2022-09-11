package dev.isxander.yacl.api;

import dev.isxander.yacl.gui.RequireRestartScreen;
import net.minecraft.client.MinecraftClient;

import java.util.function.Consumer;

/**
 * Code that is executed upon certain options being applied.
 * Each flag is executed only once per save, no matter the amount of options with the flag.
 */
@FunctionalInterface
public interface OptionFlag extends Consumer<MinecraftClient> {
    /**
     * Warns the user that a game restart is required for the changes to take effect
     */
    OptionFlag GAME_RESTART = client -> client.setScreen(new RequireRestartScreen(client.currentScreen));

    /**
     * Reloads chunks upon applying (F3+A)
     */
    OptionFlag RELOAD_CHUNKS = client -> client.worldRenderer.reload();

    OptionFlag WORLD_RENDER_UPDATE = client -> client.worldRenderer.scheduleTerrainUpdate();

    OptionFlag ASSET_RELOAD = MinecraftClient::reloadResourcesConcurrently;
}
