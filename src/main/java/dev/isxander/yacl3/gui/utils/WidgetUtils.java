package dev.isxander.yacl3.gui.utils;

import net.minecraft.client.gui.components.events.GuiEventListener;

//? if >=1.21.11 {
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
//?}

public final class WidgetUtils {

    public static boolean mouseClicked(GuiEventListener l, double mouseX, double mouseY, int button) {
        //? if >=1.21.11 {
        return l.mouseClicked(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), false);
        //?} else {
        /*return l.mouseClicked(mouseX, mouseY, button);
        *///?}
    }

    public static boolean keyPressed(GuiEventListener l, int keyCode, int scanCode, int modifiers) {
        //? if >=1.21.11 {
        return l.keyPressed(new KeyEvent(keyCode, scanCode, modifiers));
        //?} else {
        /*return l.keyPressed(keyCode, scanCode, modifiers);
        *///?}
    }

    public static boolean charTyped(GuiEventListener l, char codePoint, int modifiers) {
        //? if >=1.21.11 {
        return l.charTyped(new CharacterEvent(codePoint, modifiers));
        //?} else {
        /*return l.charTyped(codePoint, modifiers);
        *///?}
    }

    public static boolean mouseDragged(GuiEventListener l, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        //? if >=1.21.11 {
        return l.mouseDragged(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), deltaX, deltaY);
        //?} else {
        /*return l.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        *///?}
    }

    private WidgetUtils() {
    }
}
