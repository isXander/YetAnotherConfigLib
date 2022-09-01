package dev.isxander.yacl.gui.controllers.slider;

import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SliderControllerElement extends ControllerWidget<ISliderController<?>> {
    private final double min, max, interval;

    private float interpolation;

    private Dimension<Integer> sliderBounds;

    private boolean mouseDown = false;

    public SliderControllerElement(ISliderController<?> option, Screen screen, Dimension<Integer> dim, double min, double max, double interval) {
        super(option, screen, dim);
        this.min = min;
        this.max = max;
        this.interval = interval;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        calculateInterpolation();
    }

    @Override
    protected void drawHoveredControl(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // track
        DrawableHelper.fill(matrices, sliderBounds.x(), sliderBounds.centerY() - 1, sliderBounds.xLimit(), sliderBounds.centerY(), -1);
        // track shadow
        DrawableHelper.fill(matrices, sliderBounds.x() + 1, sliderBounds.centerY(), sliderBounds.xLimit() + 1, sliderBounds.centerY() + 1, 0xFF404040);

        // thumb shadow
        DrawableHelper.fill(matrices, getThumbX() - getThumbWidth() / 2 + 1, sliderBounds.y() + 1, getThumbX() + getThumbWidth() / 2 + 1, sliderBounds.yLimit() + 1, 0xFF404040);
        // thumb
        DrawableHelper.fill(matrices, getThumbX() - getThumbWidth() / 2, sliderBounds.y(), getThumbX() + getThumbWidth() / 2, sliderBounds.yLimit(), -1);
    }

    @Override
    protected void drawValueText(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        if (hovered)
            matrices.translate(-(sliderBounds.width() + 6 + getThumbWidth() / 2f), 0, 0);
        super.drawValueText(matrices, mouseX, mouseY, delta);
        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 || !sliderBounds.isPointInside((int) mouseX, (int) mouseY))
            return false;

        mouseDown = true;

        setValueFromMouse(mouseX);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0 || !sliderBounds.isPointInside((int) mouseX, (int) mouseY))
            return false;

        setValueFromMouse(mouseX);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!isMouseOver(mouseX, mouseY) || !Screen.hasShiftDown())
            return false;

        control.setPendingValue(MathHelper.clamp(control.pendingValue() + interval * amount, min, max));
        calculateInterpolation();
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (mouseDown)
            playDownSound();
        mouseDown = false;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void setValueFromMouse(double mouseX) {
        double value = (mouseX - sliderBounds.x()) / sliderBounds.width() * control.range();
        double roundedValue = min + (interval * Math.round(value / interval));
        control.setPendingValue(roundedValue);
        calculateInterpolation();
    }

    @Override
    protected int getHoveredControlWidth() {
        return sliderBounds.width() + getUnhoveredControlWidth() + 6 + getThumbWidth() / 2;
    }

    private void calculateInterpolation() {
        interpolation = (float) ((control.pendingValue() - control.min()) * 1 / control.range());
    }

    @Override
    public void setDimension(Dimension<Integer> dim) {
        super.setDimension(dim);
        sliderBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - getThumbWidth() / 2 - dim.width() / 3, dim.centerY() - 4, dim.width() / 3, 8);
    }

    private int getThumbX() {
        return (int) (sliderBounds.x() + sliderBounds.width() * interpolation);
    }

    private int getThumbWidth() {
        return 4;
    }
}
