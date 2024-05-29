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

import net.minecraft.resources.ResourceLocation;
import java.nio.file.Path;

public final class YACLPlatform {
    public static ResourceLocation parseRl(String rl) {
        /*? if >1.20.6 {*//*
        return ResourceLocation.parse(rl);
        *//*?} else {*/
        return new ResourceLocation(rl);
        /*?}*/
    }

    public static ResourceLocation rl(String path) {
        return rl("yet_another_config_lib_v3", path);
    }

    public static ResourceLocation mcRl(String path) {
        return rl("minecraft", path);
    }

    public static ResourceLocation rl(String namespace, String path) {
        /*? if >1.20.6 {*//*
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        *//*?} else {*/
        return new ResourceLocation(namespace, path);
        /*?}*/
    }

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
