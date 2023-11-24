package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;

import java.awt.*;

public class ColorPickerElement extends ControllerWidget implements GuiEventListener {
    private final ColorController colorController;
    private final ColorController.ColorControllerElement entryWidget;
    private final YACLScreen screen;
    protected MutableDimension<Integer> colorPickerDim;
    private boolean mouseDown;
//    private GuiEventListener focused;
//    private boolean dragging;

    private int outline = 1;

    private float[] HSL;
    private float hue;
    private float saturation;
    private float light;

    public ColorPickerElement(ColorController control, YACLScreen screen, Dimension<Integer> dim, ColorController.ColorControllerElement entryWidget) {
        super(control, screen, dim);
        this.colorController = control;
        this.screen = screen;
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
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY)) {
            mouseDown = true;
            setHueFromMouseX(mouseX);
            return true;
        }
        return entryWidget.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        System.out.println("yay2");
        mouseDown = false;
        return false;
    }
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        //Checks if the mouse is either over the color picker or the color controller
        if (mouseX >= colorPickerDim.x() && mouseX <= colorPickerDim.xLimit()
                && mouseY >= colorPickerDim.yLimit() && mouseY <= colorPickerDim.y()) { //y and yLimit flipped apparently?
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double d) {
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return entryWidget.charTyped(chr, modifiers);
    }

    @Override
    public void setDimension(Dimension<Integer> dim) {
        super.setDimension(dim);

        int colorPickerHeight = (dim.height() * -2) - 7;
        int colorPickerX = dim.centerX() - getXPadding() * 2;

        //A single dimension for the entire color picker as a whole
        //Would allow for the x/y(Limit) to work for the outline by adding + 1 to everything
        //Division would be used for the bigger color preview, light and saturation picker, and hue slider
        //to determine their dimensions
        //FIXME - y/yLimit are flipped?
        colorPickerDim = Dimension.ofInt(colorPickerX - outline, dim.y() - outline, (dim.width() + dim.x() - colorPickerX) - outline, colorPickerHeight- outline);
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    @Override
    protected int getHoveredControlWidth() {
        return Math.min(textRenderer.width(control.formatValue()), getUnshiftedLength());
    }

    @Override
    protected int getUnhoveredControlWidth() {
        return textRenderer.width(getValueText());
    }

    public int getUnshiftedLength() {
        if(control.option().name().getString().isEmpty())
            return getDimension().width() - getXPadding() * 2;
        return getDimension().width() / 8 * 5;
    }

    @Override
    public void unfocus() {
        super.unfocus();
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

    protected int getThumbWidth() {
        return 4;
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
//        Color newColor = Color.getHSBColor(hue, saturation, light);
//        String hex = Integer.toHexString(newColor.getRGB()).substring(2);
//        colorController.setFromString(hex);
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
        //Gets the light/brightness/value(has a few different names, all refer to the same thing) of the pending value
        return HSL[2];
    }

    protected float getRgbFromHue() {
        return Color.HSBtoRGB(hue, 1, 1);
    }
}
