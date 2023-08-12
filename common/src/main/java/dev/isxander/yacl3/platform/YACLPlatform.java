package dev.isxander.yacl3.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

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
}
