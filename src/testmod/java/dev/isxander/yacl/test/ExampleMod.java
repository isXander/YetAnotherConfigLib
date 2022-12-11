package dev.isxander.yacl.test;

import dev.isxander.yacl.test.config.ExampleConfig;
import net.fabricmc.api.ClientModInitializer;

public class ExampleMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExampleConfig.INSTANCE.load();
    }
}
