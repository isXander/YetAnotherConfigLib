package dev.isxander.yacl3.gui.utils;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class MiscUtil {
    public static <T> T getFromRegistry(Registry<T> registry, Identifier identifier) {
        //? if >=1.21.11 {
        return registry.getValue(identifier);
        //?} else {
        /*return registry.get(identifier);
        *///?}
    }
}
