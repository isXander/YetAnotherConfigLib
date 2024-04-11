package dev.isxander.yacl3.platform;

/*?if fabric {*/
import net.fabricmc.loader.api.FabricLoader;
/*?} elif neoforge {*//*
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
*//*?} elif forge {*//*
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
*//*?}*/

import java.nio.file.Path;

public final class YACLPlatform {
    public static Env getEnvironment() {
        /*?if fabric {*/
        return switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> Env.CLIENT;
            case SERVER -> Env.SERVER;
        };
        /*?} elif forge-like {*//*
        return switch (FMLEnvironment.dist) {
            case CLIENT -> Env.CLIENT;
            case DEDICATED_SERVER -> Env.SERVER;
        };
        *//*?}*/
    }

    public static Path getConfigDir() {
        /*?if fabric {*/
        return FabricLoader.getInstance().getConfigDir();
        /*?} elif forge-like {*//*
        return FMLPaths.CONFIGDIR.get();
        *//*?}*/
    }

    public static boolean isDevelopmentEnv() {
        /*?if fabric {*/
        return FabricLoader.getInstance().isDevelopmentEnvironment();
        /*?} elif forge-like {*//*
        return !FMLEnvironment.production;
        *//*?}*/
    }
}
