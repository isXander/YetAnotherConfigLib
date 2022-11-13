package dev.isxander.yacl.test.config;

import com.google.gson.Gson;
import dev.isxander.yacl.config.ConfigInstance;
import dev.isxander.yacl.config.GsonConfigInstance;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Entrypoint implements ClientModInitializer {
    private static GsonConfigInstance<ConfigData> config;

    @Override
    public void onInitializeClient() {
        config = new GsonConfigInstance<>(ConfigData.class, FabricLoader.getInstance().getConfigDir().resolve("yacl-test.json"));
        config.load();
    }

    public static ConfigInstance<ConfigData> getConfig() {
        return config;
    }
}
