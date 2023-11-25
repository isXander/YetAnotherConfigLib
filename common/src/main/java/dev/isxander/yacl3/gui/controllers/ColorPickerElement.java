package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;

import java.awt.*;

public class ColorPickerElement extends ControllerWidget<ColorController> implements GuiEventListener {
    private final ColorController colorController;
    private final ColorController.ColorControllerElement entryWidget;
    private final YACLScreen screen;
    protected MutableDimension<Integer> colorPickerDim;
    private boolean mouseDown;

    //The width of the black outline for the entire color picker
    //Space is made in between various parts of the color picker using this variable
    //example: If the outline is set to 1, then a 1 pixel wide black outline is rendered around
    //the entire color picker, the main color preview, HSL gradient, and hue gradient
    private int outline = 1;

    //The main color preview's portion of the color picker as a whole
    //example: if previewPortion is set to 7, then the color preview will take up
    //a 7th of the color picker's width
    private int previewPortion = 7;

    //The height in pixels of the hue slider
    //example: if the sliderHeight is set to 7, then the hue slider will be 7 pixels, with some extra padding between
    //the color preview and the HSL gradient to allow for an outline(determined by the "outline" int)
    private int sliderHeight = 7;

    //Hue
    private int hueSliderX;

    //Saturation & Light
    private int satLightX;
    private int satLightY;


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

        updateHSL();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

        updateHSL();

        //FIXME - If the color picker is towards the top of the category, it will appear above the color controller instead of below
        //FIXME - The color preview doesn't have enough room for the translation string

        //Main color preview
        graphics.fill(colorPickerDim.x(), colorPickerDim.y() - sliderHeight - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion), colorPickerDim.yLimit(), 2, colorController.option().pendingValue().getRGB());

        //HSL gradient

        //White to pending color's RGB from hue, left to right
        fillSidewaysGradient(graphics, colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + 1, colorPickerDim.yLimit(), 2, 0xFFFFFFFF, (int) getRgbFromHue());

        //Transparent to black, top to bottom
        graphics.fillGradient(colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + 1, colorPickerDim.yLimit(), 3,0xFF000000, 0x00000000);

        //Hue gradient

        //Hue rainbow gradient
        drawRainbowGradient(graphics, colorPickerDim.x(), colorPickerDim.y(), colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight, 2);

        //Hue slider thumb
        graphics.fill(getHueThumbX() - getThumbWidth() / 2, colorPickerDim.y(), getHueThumbX() + getThumbWidth() / 2, colorPickerDim.y() - sliderHeight, 5, -1);
        //Hue slider thumb shadow
        graphics.fill(getHueThumbX() - getThumbWidth() / 2 - 1, colorPickerDim.y() + 1, getHueThumbX() + getThumbWidth() / 2 + 1, colorPickerDim.y() - sliderHeight - 1, 4, 0xFF404040);


        //Outline
        //Simply draws a huge black box
        //Space was added between the color preview, HSL gradient, and hue gradient earlier
        graphics.fill(colorPickerDim.x() - outline, colorPickerDim.y() + outline, colorPickerDim.xLimit() + outline, colorPickerDim.yLimit() - outline, 1, 0xFF000000);
    }

    public boolean clickedHueSlider(double mouseX, double mouseY) {
        if(mouseY <= colorPickerDim.y() && mouseY >= colorPickerDim.y() - sliderHeight) {
            //mini workaround for holding the mouse down past the x/xLimit
            return mouseDown || mouseX >= colorPickerDim.x() && mouseX <= colorPickerDim.xLimit();
        }

        return false;
    }

    public void setColorFromMouseClick(double mouseX, double mouseY) {
        //TODO - Allow for the mouse to be held down past the box, while not messing up the other part of the color picker
        if(clickedHueSlider(mouseX, mouseY)) {
            setHueFromMouseX(mouseX);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY)) {
            mouseDown = true;
//            setHueFromMouseX(mouseX);
            setColorFromMouseClick(mouseX, mouseY);
            return true;
        }
        return entryWidget.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(mouseDown || isMouseOver(mouseX, mouseY)) {
            setColorFromMouseClick(mouseX, mouseY);
            return true;
        }
        return entryWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount, double d) {
        //Use to allow for small adjustments of the color?
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //Done to allow for typing whilst the color picker is visible
        return entryWidget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        //Done to allow for typing whilst the color picker is visible
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

    protected int getHueThumbX() {
        int min = colorPickerDim.x();
        int max = colorPickerDim.xLimit();
        int value = (int) (colorPickerDim.x() + colorPickerDim.width() * this.hue);

        return Mth.clamp(value, min, max);
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
            //Sets the color to FF0001, which allows for the hue thumb slider to be at the very edge of the right
            this.hue = 0.9991f;
        } else {
            float newHue = ((float) (mouseX - colorPickerDim.x()) / colorPickerDim.width());

            this.hue = Mth.clamp(newHue, 0f, 0.9991f);
        }

        setColorControllerFromHSL();
    }

    public void setColorControllerFromHSL() {
        //Updates the current color controller's pending value based from HSL to RGB
        colorController.option().requestSet(Color.getHSBColor(hue, saturation, light));
    }

    protected void updateHSL() {
        this.HSL = getHSL();
        this.hue = getHue();
        this.saturation = getSaturation();
        this.light = getLight();
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
