package dev.isxander.yacl.test.config;

import com.google.gson.Gson;
import dev.isxander.yacl.config.ConfigInstance;
import dev.isxander.yacl.config.GsonConfigInstance;
import dev.isxander.yacl.config.YACLConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Entrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        YACLConfigManager.register(new GsonConfigInstance<>(ConfigData.class, FabricLoader.getInstance().getConfigDir().resolve("yacl-test.json"), new Gson()));
    }

    public static ConfigInstance<ConfigData> getConfig() {
        return YACLConfigManager.getConfigInstance(ConfigData.class);
    }
}
