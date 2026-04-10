package dev.isxander.yacl3.gui.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public final class KeyUtils {

    public static boolean hasShiftDown() {
        var window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT) ||
               InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT);
    }

    public static boolean hasControlDown() {
        var window = Minecraft.getInstance().getWindow();
        return InputConstants.isKeyDown(window, InputConstants.KEY_LCONTROL) ||
               InputConstants.isKeyDown(window, InputConstants.KEY_RCONTROL);
    }

    private KeyUtils() {
    }
}
