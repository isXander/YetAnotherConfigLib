package dev.isxander.yacl.gui.controllers.cycling;

import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

public class CyclingControllerElement extends ControllerWidget<ICyclingController<?>> {

    public CyclingControllerElement(ICyclingController<?> control, YACLScreen screen, Dimension<Integer> dim) {
        super(control, screen, dim);
    }

    public void cycleValue(int increment) {
        int targetIdx = control.getPendingValue() + increment;
        if (targetIdx >= control.getCycleLength()) {
            targetIdx -= control.getCycleLength();
        } else if (targetIdx < 0) {
            targetIdx += control.getCycleLength();
        }
        control.setPendingValue(targetIdx);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY) || (button != 0 && button != 1) || !isAvailable())
            return false;

        playDownSound();
        cycleValue(button == 1 || Screen.hasShiftDown() || Screen.hasControlDown() ? -1 : 1);

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused)
            return false;

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_DOWN ->
                    cycleValue(-1);
            case GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_UP ->
                    cycleValue(1);
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_KP_ENTER ->
                    cycleValue(Screen.hasControlDown() || Screen.hasShiftDown() ? -1 : 1);
            default -> {
                return false;
            }
        }

        return true;
    }

    @Override
    protected int getHoveredControlWidth() {
        return getUnhoveredControlWidth();
    }
}
