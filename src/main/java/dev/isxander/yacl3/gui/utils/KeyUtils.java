package dev.isxander.yacl3.gui.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class KeyUtils {

    public static boolean isSelection(int input) {
        return input == GLFW.GLFW_KEY_ENTER || input == GLFW.GLFW_KEY_SPACE || input == GLFW.GLFW_KEY_KP_ENTER;
    }

    public static boolean isConfirmation(int input) {
        return input == GLFW.GLFW_KEY_ENTER || input == GLFW.GLFW_KEY_KP_ENTER;
    }

    public static boolean isEscape(int input) {
        return input == GLFW.GLFW_KEY_ESCAPE;
    }

    public static boolean isLeft(int input) {
        return input == GLFW.GLFW_KEY_LEFT;
    }

    public static boolean isRight(int input) {
        return input == GLFW.GLFW_KEY_RIGHT;
    }

    public static boolean isUp(int input) {
        return input == GLFW.GLFW_KEY_UP;
    }

    public static boolean isDown(int input) {
        return input == GLFW.GLFW_KEY_DOWN;
    }

    public static boolean isCycleFocus(int input) {
        return input == GLFW.GLFW_KEY_TAB;
    }

    public static int getDigit(int input) {
        int i = input - GLFW.GLFW_KEY_0;
        return i >= 0 && i <= 9 ? i : -1;
    }

    public static boolean hasAltDown(int modifiers) {
        return (modifiers & GLFW.GLFW_MOD_ALT) != 0;
    }

    public static boolean hasShiftDown(int modifiers) {
        return (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
    }

    public static boolean hasShiftDown() {
        var window = /*? if >=1.21.11 {*/ Minecraft.getInstance().getWindow(); /*?} else {*/ /*Minecraft.getInstance().getWindow().getWindow(); *//*?}*/
        return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT) ||
               InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean hasControlDown(int modifiers) {
        return (modifiers & (Util.getPlatform() == Util.OS.OSX ? GLFW.GLFW_MOD_SUPER : GLFW.GLFW_MOD_CONTROL)) != 0;
    }

    public static boolean hasControlDown() {
        var window = /*? if >=1.21.11 {*/ Minecraft.getInstance().getWindow(); /*?} else {*/ /*Minecraft.getInstance().getWindow().getWindow(); *//*?}*/
        return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_CONTROL) ||
               InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isSelectAll(int input, int modifiers) {
        return input == GLFW.GLFW_KEY_A && hasControlDown(modifiers) && !hasShiftDown(modifiers) && !hasAltDown(modifiers);
    }

    public static boolean isCopy(int input, int modifiers) {
        return input == GLFW.GLFW_KEY_C && hasControlDown(modifiers) && !hasShiftDown(modifiers) && !hasAltDown(modifiers);
    }

    public static boolean isPaste(int input, int modifiers) {
        return input == GLFW.GLFW_KEY_V && hasControlDown(modifiers) && !hasShiftDown(modifiers) && !hasAltDown(modifiers);
    }

    public static boolean isCut(int input, int modifiers) {
        return input == GLFW.GLFW_KEY_X && hasControlDown(modifiers) && !hasShiftDown(modifiers) && !hasAltDown(modifiers);
    }

    private KeyUtils() {
    }
}
