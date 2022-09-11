package dev.isxander.yacl.impl;

import dev.isxander.yacl.api.Storage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class MinecraftOptionsStorage implements Storage<GameOptions> {
    public static final MinecraftOptionsStorage INSTANCE = new MinecraftOptionsStorage();

    @Override
    public GameOptions data() {
        return MinecraftClient.getInstance().options;
    }

    @Override
    public void save() {
        YACLConstants.LOGGER.info("Saving Minecraft Options");
        data().write();
    }

    @Override
    public void load() {
        // TODO: not sure if this is a good idea or not
        // data().load();
    }
}
