package dev.isxander.yacl3.platform;

/*? if fabric {*/
import net.fabricmc.loader.api.FabricLoader;
/*?} elif neoforge {*/
/*import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
*//*?} elif forge {*/
/*import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
*//*?}*/

import net.minecraft.resources.ResourceLocation;
import java.nio.file.Path;

public final class YACLPlatform {
    public static ResourceLocation parseRl(String rl) {
        return ResourceLocation.parse(rl);
    }

    public static ResourceLocation rl(String path) {
        return rl("yet_another_config_lib_v3", path);
    }

    public static ResourceLocation mcRl(String path) {
        return rl("minecraft", path);
    }

    public static ResourceLocation rl(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static Env getEnvironment() {
        /*? if fabric {*/
        return switch (FabricLoader.getInstance().getEnvironmentType()) {
            case CLIENT -> Env.CLIENT;
            case SERVER -> Env.SERVER;
        };
        /*?} elif forgelike {*/
        /*//? if >=1.21.9 {
        var dist = FMLEnvironment.getDist();
        //?} else {
        /^var dist = FMLEnvironment.dist;
        ^///?}
        return switch (dist) {
            case CLIENT -> Env.CLIENT;
            case DEDICATED_SERVER -> Env.SERVER;
        };
        *//*?}*/
    }

    public static Path getConfigDir() {
        /*? if fabric {*/
        return FabricLoader.getInstance().getConfigDir();
        /*?} elif forgelike {*/
        /*return FMLPaths.CONFIGDIR.get();
        *//*?}*/
    }

    public static boolean isDevelopmentEnv() {
        //? if fabric {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
        //?} elif forgelike {
        /*//? if >=1.21.9 {
        return !FMLEnvironment.isProduction();
        //?} else {
        /^return !FMLEnvironment.production;
        ^///?}
        *///?}
    }
}
