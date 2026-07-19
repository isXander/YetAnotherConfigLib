package dev.isxander.yacl3.gui.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public final class KeyUtils {

    public static boolean hasShiftDown() {
        return isKeyDown(InputConstants.KEY_LSHIFT) ||
               isKeyDown(InputConstants.KEY_RSHIFT);
    }

    public static boolean hasControlDown() {
        return isKeyDown(InputConstants.KEY_LCONTROL) ||
               isKeyDown(InputConstants.KEY_RCONTROL);
    }

    public static boolean isKeyDown(int key) {
        //? if >=26.3 {
        /*return InputConstants.isKeyDown(key);
        *///?} else {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), key);
        //?}
    }

    private KeyUtils() {
    }
}
