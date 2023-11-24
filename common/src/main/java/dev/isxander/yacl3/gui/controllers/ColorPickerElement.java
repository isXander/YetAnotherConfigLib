package dev.isxander.yacl3.gui.controllers;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TooltipButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class ColorPickerElement extends StringControllerElement implements GuiEventListener {
    private boolean mouseDown;
    private final ColorController colorController;
    private final ColorController.ColorControllerElement entryWidget;
    protected Dimension<Integer> inputFieldBounds;
    protected MutableDimension<Integer> colorPickerDim;
    private Dimension<Integer> sliderBounds;

    private GuiEventListener focused;
    private boolean dragging;

    private int outline = 1;

    private float[] HSL;
    private float hue;
    private float saturation;
    private float light;

    public ColorPickerElement(ColorController control, YACLScreen screen, Dimension<Integer> dim, ColorController.ColorControllerElement entryWidget) {
        super(control, screen, dim, true);
        this.colorController = control;
        this.entryWidget = entryWidget;

        setDimension(dim);

        this.HSL = getHSL();
        this.hue = getHue();
        this.saturation = getSaturation();
        this.light = getLight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

//        toggleColorPickerButton.render(graphics, mouseX, mouseY, delta);

        //FIXME - If the color picker is towards the top of the category, it will appear above the color controller instead of below
        //FIXME - The color preview doesn't have enough room for the translation string

        //The main color preview's portion of the color picker as a whole
        //example: if previewPortion is equal to 7, then the color preview will take up
        //a 7th of the color picker's width
        int previewPortion = 7;

        //The height in pixels of the hue slider
        //example: if the sliderHeight is equal to 7, then the hue slider will be 7 pixels, with some extra padding between
        //the color preview and the HSL gradient to allow for an outline(determined by the "outline" int)
        int sliderHeight = 7;

        //Main color preview
        graphics.fill(colorPickerDim.x(), colorPickerDim.y() - sliderHeight - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion), colorPickerDim.yLimit(), 2, colorController.option().pendingValue().getRGB());

        //HSL gradient

        //White to pending color's RGB, left to right
        fillSidewaysGradient(graphics, colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + 1, colorPickerDim.yLimit(), 2, 0xFFFFFFFF, (int) getRgbFromHue());

        //Transparent to black, top to bottom
        graphics.fillGradient(colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + 1, colorPickerDim.yLimit(), 3,0xFF000000, 0x00000000);

        //Hue slider
        drawRainbowGradient(graphics, colorPickerDim.x(), colorPickerDim.y(), colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight, 2);


        //Slider thumb
        graphics.fill(getThumbX(mouseX) - getThumbWidth() / 2, colorPickerDim.y(), getThumbX(mouseX) + getThumbWidth() / 2, colorPickerDim.y() - sliderHeight, 5, -1);
        //Slider thumb shadow
        graphics.fill(getThumbX(mouseX) - getThumbWidth() / 2 - 1, colorPickerDim.y() + 1, getThumbX(mouseX) + getThumbWidth() / 2 + 1, colorPickerDim.y() - sliderHeight - 1, 4, 0xFF404040);


        //Outline
        //Simply draws a huge black box
        //Space was added between the color preview, HSL gradient, and rainbow gradients earlier
        graphics.fill(colorPickerDim.x() - outline, colorPickerDim.y() + outline, colorPickerDim.xLimit() + outline, colorPickerDim.yLimit() - outline, 1, 0xFF000000);

//        if(mouseDown) {
//            setHueFromMouseX(mouseX);
//        }


        //Old mouse detection stuff
        //DELETEME

        //FIXME - It would be much more ideal for the mouseClicked override to handle this instead
        //Methods used to determine mouse clicks

        //Temporary workaround for mouseClicked not working
        //This detects when the left mouse button has been clicked anywhere on screen
//        int button = GLFW.glfwGetMouseButton(this.client.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
//        mouseDown = button == GLFW.GLFW_PRESS;
//            if(button == GLFW.GLFW_PRESS) {
//                System.out.println("yay");
//                //TODO - mouseDown boolean enabling/disabling
//            }

//        if(mouseDown) {
            //Using the mouseClicked "override"(more like a method in this case in the meantime)
            //to make it easier for readability and for the future if the mouseClicked override is fixable
//            mouseClicked(mouseX, mouseY, 0);
//            setHueFromX(mouseX);
//        }
    }

    @Override
    public void setDimension(Dimension<Integer> dim) {
        super.setDimension(dim);

        int width = Math.max(6, Math.min(textRenderer.width(getValueText()), getUnshiftedLength()));


        int trackWidth = dim.width() / 3;
        if (optionNameString.isEmpty())
            trackWidth = dim.width() / 2;


//        sliderBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - getThumbWidth() / 2 - trackWidth, dim.centerY() - 5, trackWidth, 10);
        inputFieldBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - width, dim.centerY() - textRenderer.lineHeight / 2, width, textRenderer.lineHeight);

        int colorPickerHeight = (dim.height() * -2) - 7;
        int colorPickerX = dim.centerX() - getXPadding() * 2;

        //A single dimension for the entire color picker as a whole
        //Would allow for the x/y(Limit) to work for the outline by adding + 1 to everything
        //Division would be used for the bigger color preview, light and saturation picker, and hue slider
        //to determine their dimensions
        //FIXME - y/yLimit are flipped?
        colorPickerDim = Dimension.ofInt(colorPickerX - outline, dim.y() - outline, (dim.width() + dim.x() - colorPickerX) - outline, colorPickerHeight- outline);
    }

    //SLIDER related overrides

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
    }

    public int getUnshiftedLength() {
        if(control.option().name().getString().isEmpty())
            return getDimension().width() - getXPadding() * 2;
        return getDimension().width() / 8 * 5;
    }

    @Override
    protected int getHoveredControlWidth() {
//            return sliderBounds.width() + getUnhoveredControlWidth() + 6 + getThumbWidth() / 2;
        return Math.min(textRenderer.width(control.formatValue()), getUnshiftedLength());
    }

    @Override
    protected int getUnhoveredControlWidth() {
        return textRenderer.width(getValueText());
    }

    public void setMouseDown(boolean mouseDown) {
        this.mouseDown = mouseDown;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if(isMouseOver(mouseX, mouseY)) {
            setHueFromMouseX(mouseX);
            return true;
//        } else if (entryWidget.mouseClicked(mouseX, mouseY, button)) {
//            return true;
        }

//        return entryWidget.mouseClicked(mouseX, mouseY, button);
//        entryWidget.setColorPickerVisible(false);
        return entryWidget.mouseClicked(mouseX, mouseY, button);
//        selfDestruct();
//        return false;


//        entryWidget.removeColorPicker();
//        selfDestruct();
//        return false;


//        if (mouseX >= colorPickerDim.x() && mouseX <= colorPickerDim.xLimit()
//                && mouseY >= colorPickerDim.yLimit() && mouseY <= colorPickerDim.y()) { //y and yLimit flipped apparently?
//            return true;
//        }

        //old mouse clicking stuff from before dimension overhaul
        //DELETEME
//        return false;
//        return super.mouseClicked(mouseX, mouseY, button);

//        if(mouseDown) {
//            //TODO - Replace all variables like this with private vars?
//            int hueSliderX = colorPickerDim.x() - 30 - inputFieldBounds.width() - 40;
//            int hueSliderY = colorPickerDim.y() + 10;
//            int hueSliderXLimit = inputFieldBounds.xLimit() + 5;
//            int hueSliderYLimit = colorPickerDim.yLimit() + 8;
////                System.out.println("button: " +  button);
////                System.out.println("x: " + mouseX + "y: " + mouseY);
////                System.out.println("x: " + hueSliderX + "y:" + hueSliderY);
////                System.out.println("xLimit: " + hueSliderXLimit + "yLimit: " + hueSliderYLimit);
//
//            //Detects if the user has clicked the hue slider
//            if((mouseX >= hueSliderX && mouseX <= hueSliderXLimit)
//                    && (mouseY >= hueSliderY && mouseY <= hueSliderYLimit)) {
//                System.out.println("yay");
//                setHueFromMouse(mouseX);
//                return true;
//            }
//        }
//        return false;
    }

    public void selfDestruct() {
//        screen.removeColorPickerWidget(this);
//        entryWidget.setColorPickerVisible(false);
    }

    @Override
    public void unfocus() {
        super.unfocus();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        System.out.println("yay2");
        mouseDown = false;
        return false;
//            return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        System.out.println("yay3");
        return true;
//            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

//    @Override
//    public boolean isDragging() {
//        return dragging;
//    }
//
//    @Override
//    public void setDragging(boolean dragging) {
//        this.dragging = dragging;
//    }
//
//    @Nullable
//    @Override
//    public GuiEventListener getFocused() {
//        return this.focused;
//    }
//
//    @Override
//    public void setFocused(@Nullable GuiEventListener child) {
//        this.focused = focused;
//    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {

        //Checks if the mouse is either over the color picker or the color controller
        if (mouseX >= colorPickerDim.x() && mouseX <= colorPickerDim.xLimit()
                && mouseY >= colorPickerDim.yLimit() && mouseY <= colorPickerDim.y()) { //y and yLimit flipped apparently?
            return true;
        }
//        return super.isMouseOver(mouseX, mouseY);
//        return controllerElement.isMouseOver(mouseX, mouseY);
        return false;
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

//    @Override
//    public boolean isFocused() {
//        return true;
//    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double d) {
        return super.mouseScrolled(mouseX, mouseY, amount, d);
    }

    public boolean shouldStayVisible(double mouseX, double mouseY) {
        if(entryWidget.clickedColorPreview(mouseX, mouseY)) {
            return false;
        }
        return isMouseOver(mouseX, mouseY) || entryWidget.isMouseOver(mouseX, mouseY);
    }

    protected int getThumbX(int mouseX) {
        int min = colorPickerDim.x();
        int max = colorPickerDim.xLimit();

        return Mth.clamp(mouseX, min, max);
//        if(min < mouseX) {
////            mouseX -= min;
//            return (mouseX < max) ? mouseX : max;
//        }
//        return min;

        //Calculates the thumb x based upon the pending value's hue
        //Multiplying the adjustment by 1.9 instead of 2 seemed to give better results
//        double multiplyValue = 1.9;
//        int adjustmentValue = (int) ((inputFieldBounds.xLimit() + 5 - colorPickerDim.x() - 30) * getHue() * multiplyValue);

        //TODO - Make the thumb appear at the mouse's pos. clamped on the slider x/xLimit
//        if(mouseDown) {
//            return Mth.clamp()
//        }

//        return Mth.clamp(colorPickerDim.x() - 30 + adjustmentValue, colorPickerDim.x() - 30, inputFieldBounds.xLimit() + 5);
    }

    public void setHueFromMouseX(double mouseX) {
        //Changes the hue of the pending color based on the mouseX's pos.
        //relative to the colorPickerDim's x/xLimit
        if(mouseX < colorPickerDim.x()) {
            this.hue = 0f;
        } else if (mouseX > colorPickerDim.xLimit()) {
            this.hue = 1f;
        } else {
            float newHue = ((float) (mouseX - colorPickerDim.x()) / colorPickerDim.width());

            this.hue = Mth.clamp(newHue, 0f, 1.0f);
        }

        setColorControllerFromHSL();
    }

    public void setColorControllerFromHSL() {
        //Updates the current color controller's pending value based from HSL to RGB
        colorController.option().requestSet(Color.getHSBColor(hue, saturation, light));
    }

    protected float[] getHSL() {
        Color pendingValue = colorController.option().pendingValue();
        return Color.RGBtoHSB(pendingValue.getRed(), pendingValue.getGreen(), pendingValue.getBlue(), null);
    }

    protected float getHue() {
        //Gets the hue of the pending value
        return HSL[0];
    }

    protected float getSaturation() {
        //Gets the saturation of the pending value
        return HSL[1];
    }

    protected float getLight() {
        //Gets the light/brightness/value of the pending value
        return HSL[2];
    }

    protected float getRgbFromHue() {
        return Color.HSBtoRGB(hue, 1, 1);
    }

    protected int getThumbWidth() {
        return 4;
    }
}
