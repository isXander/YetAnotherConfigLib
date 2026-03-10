package dev.isxander.yacl3.gui.controllers.slider;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.gui.utils.KeyUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        calculateInterpolation();
    }

    @Override
    protected void drawHoveredControl(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // track
        graphics.fill(sliderBounds.x(), sliderBounds.centerY() - 1, sliderBounds.xLimit(), sliderBounds.centerY(), -1);
        // track shadow
        graphics.fill(sliderBounds.x() + 1, sliderBounds.centerY(), sliderBounds.xLimit() + 1, sliderBounds.centerY() + 1, 0xFF404040);

        // thumb shadow
        graphics.fill(getThumbX() - getThumbWidth() / 2 + 1, sliderBounds.y() + 1, getThumbX() + getThumbWidth() / 2 + 1, sliderBounds.yLimit() + 1, 0xFF404040);
        // thumb
        graphics.fill(getThumbX() - getThumbWidth() / 2, sliderBounds.y(), getThumbX() + getThumbWidth() / 2, sliderBounds.yLimit(), -1);

        if (isHoveredSliderBounds(mouseX, mouseY)) {
            graphics.requestCursor(isAvailable() ? com.mojang.blaze3d.platform.cursor.CursorTypes.RESIZE_EW : com.mojang.blaze3d.platform.cursor.CursorTypes.NOT_ALLOWED);
        }
    }

    @Override
    protected void drawValueText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.pose().pushMatrix();
        if (isHovered())
            graphics.pose().translate(-(sliderBounds.width() + 6 + getThumbWidth() / 2f), 0);
        super.drawValueText(graphics, mouseX, mouseY, delta);
        graphics.pose().popMatrix();
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (!isAvailable() || event.button() != 0 || !isHoveredSliderBounds(event.x(), event.y()))
            return false;

        mouseDown = true;

        setValueFromMouse(event.x());
        return true;
    }

    private boolean isHoveredSliderBounds(double mouseX, double mouseY) {
        return sliderBounds.isPointInside((int) mouseX, (int) mouseY);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (!isAvailable() || event.button() != 0 || !mouseDown)
            return false;

        setValueFromMouse(event.x());
        return true;
    }

    public void incrementValue(double amount) {
        control.setPendingValue(Mth.clamp(control.pendingValue() + interval * amount, min, max));
        calculateInterpolation();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (!isAvailable() || (!isMouseOver(mouseX, mouseY)) || (!KeyUtils.hasShiftDown() && !KeyUtils.hasControlDown()))
            return false;

        incrementValue(vertical);
        return true;
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (isAvailable() && mouseDown)
            playDownSound();
        mouseDown = false;

        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent event) {
        if (!focused)
            return false;

        switch (event.key()) {
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
