package dev.isxander.yacl.gui.controllers.slider;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

public class SliderControllerElement extends ControllerWidget<ISliderController<?>> {
    private final double min, max, interval;

    private float interpolation;

    private Dimension<Integer> sliderBounds;

    private boolean mouseDown = false;

    public SliderControllerElement(ISliderController<?> option, YACLScreen screen, Dimension<Integer> dim, double min, double max, double interval) {
        super(option, screen, dim);
        this.min = min;
        this.max = max;
        this.interval = interval;
        setDimension(dim);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        calculateInterpolation();
    }

    @Override
    protected void drawHoveredControl(PoseStack matrices, int mouseX, int mouseY, float delta) {
        // track
        GuiComponent.fill(matrices, sliderBounds.x(), sliderBounds.centerY() - 1, sliderBounds.xLimit(), sliderBounds.centerY(), -1);
        // track shadow
        GuiComponent.fill(matrices, sliderBounds.x() + 1, sliderBounds.centerY(), sliderBounds.xLimit() + 1, sliderBounds.centerY() + 1, 0xFF404040);

        // thumb shadow
        GuiComponent.fill(matrices, getThumbX() - getThumbWidth() / 2 + 1, sliderBounds.y() + 1, getThumbX() + getThumbWidth() / 2 + 1, sliderBounds.yLimit() + 1, 0xFF404040);
        // thumb
        GuiComponent.fill(matrices, getThumbX() - getThumbWidth() / 2, sliderBounds.y(), getThumbX() + getThumbWidth() / 2, sliderBounds.yLimit(), -1);
    }

    @Override
    protected void drawValueText(PoseStack matrices, int mouseX, int mouseY, float delta) {
        matrices.pushPose();
        if (isHovered())
            matrices.translate(-(sliderBounds.width() + 6 + getThumbWidth() / 2f), 0, 0);
        super.drawValueText(matrices, mouseX, mouseY, delta);
        matrices.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isAvailable() || button != 0 || !sliderBounds.isPointInside((int) mouseX, (int) mouseY))
            return false;

        mouseDown = true;

        setValueFromMouse(mouseX);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isAvailable() || button != 0 || !mouseDown)
            return false;

        setValueFromMouse(mouseX);
        return true;
    }

    public void incrementValue(double amount) {
        control.setPendingValue(Mth.clamp(control.pendingValue() + interval * amount, min, max));
        calculateInterpolation();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isAvailable() || (!isMouseOver(mouseX, mouseY)) || (!Screen.hasShiftDown() && !Screen.hasControlDown()))
            return false;

        incrementValue(amount);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isAvailable() && mouseDown)
            playDownSound();
        mouseDown = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused)
            return false;

        switch (keyCode) {
            case InputConstants.KEY_LEFT -> incrementValue(-1);
            case InputConstants.KEY_RIGHT -> incrementValue(1);
            default -> {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY) || mouseDown;
    }

    protected void setValueFromMouse(double mouseX) {
        double value = (mouseX - sliderBounds.x()) / sliderBounds.width() * control.range();
        control.setPendingValue(roundToInterval(value));
        calculateInterpolation();
    }

    protected double roundToInterval(double value) {
        return Mth.clamp(min + (interval * Math.round(value / interval)), min, max); // extremely imprecise, requires clamping
    }

    @Override
    protected int getHoveredControlWidth() {
        return sliderBounds.width() + getUnhoveredControlWidth() + 6 + getThumbWidth() / 2;
    }

    protected void calculateInterpolation() {
        interpolation = Mth.clamp((float) ((control.pendingValue() - control.min()) * 1 / control.range()), 0f, 1f);
    }

    @Override
    public void setDimension(Dimension<Integer> dim) {
        super.setDimension(dim);
        int trackWidth = dim.width() / 3;
        if (optionNameString.isEmpty())
            trackWidth = dim.width() / 2;

        sliderBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - getThumbWidth() / 2 - trackWidth, dim.centerY() - 5, trackWidth, 10);
    }

    protected int getThumbX() {
        return (int) (sliderBounds.x() + sliderBounds.width() * interpolation);
    }

    protected int getThumbWidth() {
        return 4;
    }
}
