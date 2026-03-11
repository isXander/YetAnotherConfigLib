package dev.isxander.yacl3.gui.controllers.cycling;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

public class CyclingControllerElement extends ControllerWidget<ICyclingController<?>> {

    public CyclingControllerElement(ICyclingController<?> control, YACLScreen screen, Dimension<Integer> dim) {
        super(control, screen, dim);
    }

    @Override
    protected void extractValueText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractValueText(graphics, mouseX, mouseY, a);

        if (this.hovered) {
            graphics.requestCursor(isAvailable() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
        }
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!isMouseOver(event.x(), event.y()) || (event.button() != 0 && event.button() != 1) || !isAvailable())
            return false;

        playDownSound();
        cycleValue(event.button() == 1 || event.hasShiftDown() || event.hasControlDown() ? -1 : 1);

        return true;
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (!focused)
            return false;

        switch (event.key()) {
            case InputConstants.KEY_LEFT ->
                    cycleValue(-1);
            case InputConstants.KEY_RIGHT ->
                    cycleValue(1);
            case InputConstants.KEY_RETURN, InputConstants.KEY_SPACE, InputConstants.KEY_NUMPADENTER ->
                    cycleValue(event.hasControlDown() || event.hasShiftDown() ? -1 : 1);
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
