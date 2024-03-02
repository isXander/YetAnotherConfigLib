package dev.isxander.yacl3.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

public final class YACLPlatform {
    @ExpectPlatform
    public static Env getEnvironment() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDevelopmentEnv() {
        throw new AssertionError();
    }

    public static ResourceLocation rl(String path) {
        return new ResourceLocation("yet_another_config_lib_v3", path);
    }
}
