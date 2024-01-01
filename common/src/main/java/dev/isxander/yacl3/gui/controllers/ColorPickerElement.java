package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.awt.*;

public class ColorPickerElement extends ControllerWidget<ColorController> implements GuiEventListener {
    //Full path: resources/assets/yet_another_config_lib/textures/gui/sprites/controller/colorpicker.png and colorpicker.png.mcmeta
    private static final ResourceLocation COLOR_PICKER_LOCATION = new ResourceLocation("yet_another_config_lib", "controller/colorpicker");
    private final ColorController colorController;
    private final ColorController.ColorControllerElement entryWidget;
    private final YACLScreen screen;
    protected MutableDimension<Integer> colorPickerDim;
    protected MutableDimension<Integer> previewColorDim;
    protected MutableDimension<Integer> saturationLightDim;
    protected MutableDimension<Integer> hueGradientDim;
    private boolean mouseDown;
    private boolean hueSliderDown;
    private boolean satLightGradientDown;
    private int hueThumbX;
    private int satLightThumbX;
    private boolean charTyped;

    //The width of the outline between each color picker element(color preview, saturation/light gradient, hue gradient)
    //Note: Additional space may need to be manually made upon increasing the outline
    int outline = 1;

    //The main color preview's portion of the color picker as a whole
    //example: if previewPortion is set to 7, then the color preview will take up
    //a 7th of the color picker's width
    int previewPortion = 7;

    //The height in pixels of the hue slider
    //example: if the sliderHeight is set to 7, then the hue slider will be 7 pixels, with some extra padding between
    //the color preview and the HSL gradient to allow for an outline(determined by the "outline" int)
    int sliderHeight = 7;

    //The x padding between the color preview and saturation/light gradient.
    //Does NOT account for the outline on its own
    int paddingX = 1;

    //The y padding between the hue gradient and color preview & saturation/light gradient.
    //Does NOT account for the outline on its own
    int paddingY = 3;


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
        setThumbX();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
//        setDimension(entryWidget.getDimension());
        updateHSL();


        //FIXME/FEAT - If the color picker is towards the top of the category, it will appear above the color controller instead of below

        //Main color preview
        graphics.fill(colorPickerDim.x(), colorPickerDim.y(), colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) - paddingX, colorPickerDim.yLimit() - sliderHeight - paddingY, 3, colorController.option().pendingValue().getRGB());
        //outline
        graphics.fill(colorPickerDim.x() - outline, colorPickerDim.y() - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) - paddingX + outline, colorPickerDim.yLimit() - sliderHeight - paddingY + outline, 2, Color.black.getRGB());

        //Saturation/light gradient

        //White to pending color's RGB from hue, left to right
        fillSidewaysGradient(graphics, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX + 1, colorPickerDim.y(), colorPickerDim.xLimit(), colorPickerDim.yLimit() - sliderHeight - paddingY, 3, (int) getRgbFromHueX(), 0xFFFFFFFF);
        //Transparent to black, top to bottom
        graphics.fillGradient(colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX + 1, colorPickerDim.y(), colorPickerDim.xLimit(), colorPickerDim.yLimit() - sliderHeight - paddingY, 4,0x00000000, 0xFF000000);
        //Sat/light thumb - extra 1 pixel on left and top to make it centered
        graphics.fill(satLightThumbX - getThumbWidth() / 2 - 1, getSatLightThumbY() + getThumbHeight() / 2 + 1, satLightThumbX + getThumbWidth() / 2, getSatLightThumbY() - getThumbHeight() / 2, 7, -1);
        //Sat/light thumb shadow
        graphics.fill(satLightThumbX - getThumbWidth() / 2 - 2, getSatLightThumbY() + getThumbHeight() / 2 + 2, satLightThumbX + getThumbWidth() / 2 + 1, getSatLightThumbY() - getThumbHeight() / 2 - 1, 6, 0xFF404040);

        //outline
        graphics.fill(colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX + 1 - outline, colorPickerDim.y() - outline, colorPickerDim.xLimit() + outline, colorPickerDim.yLimit() - sliderHeight - paddingY + outline, 2, Color.black.getRGB());

        //Hue gradient

        //Hue rainbow gradient
        drawRainbowGradient(graphics, colorPickerDim.x(), colorPickerDim.yLimit(), colorPickerDim.xLimit(), colorPickerDim.yLimit() - sliderHeight, 3);
        //Hue slider thumb
        graphics.fill(hueThumbX - getThumbWidth() / 2, colorPickerDim.yLimit() + outline, hueThumbX + getThumbWidth() / 2, colorPickerDim.yLimit() - sliderHeight - outline, 5, -1);
        //Hue slider thumb shadow
        graphics.fill(hueThumbX - getThumbWidth() / 2 - 1, colorPickerDim.yLimit() + outline + 1, hueThumbX + getThumbWidth() / 2 + 1, colorPickerDim.yLimit() - sliderHeight - outline - 1, 4, 0xFF404040);
        //outline
        graphics.fill(colorPickerDim.x() - outline, colorPickerDim.yLimit() + outline, colorPickerDim.xLimit() + outline, colorPickerDim.yLimit() - sliderHeight - outline, 2, Color.black.getRGB());

        //Background
        graphics.blitSprite(COLOR_PICKER_LOCATION, colorPickerDim.x() - 5, colorPickerDim.y() - 5, 1, colorPickerDim.width() + 10, colorPickerDim.height() + 10);
    }

    public boolean clickedHueSlider(double mouseX, double mouseY) {
        if(satLightGradientDown) return false;

        if(mouseY <= colorPickerDim.yLimit() && mouseY >= colorPickerDim.yLimit() - sliderHeight) {
            if(mouseX >= colorPickerDim.x() && mouseX <= colorPickerDim.xLimit()) {
                hueSliderDown = true;
            }
        }

        if(hueSliderDown) {
            hueThumbX = (int) Mth.clamp(mouseX, colorPickerDim.x(), colorPickerDim.xLimit());
        }

        return hueSliderDown;
    }

    public boolean clickedSatLightGradient(double mouseX, double mouseY) {
        if(hueSliderDown) return false;

        if(mouseX >= colorPickerDim.x() + ((double) colorPickerDim.xLimit() / previewPortion) + paddingX && mouseX <= colorPickerDim.xLimit()) {
            if(mouseY >= colorPickerDim.y() && mouseY <= colorPickerDim.yLimit() - sliderHeight - paddingY) {
                satLightGradientDown = true;
            }
        }

        if(satLightGradientDown) {
            satLightThumbX = (int) Mth.clamp(mouseX, colorPickerDim.x() + ((double) colorPickerDim.xLimit() / previewPortion) + paddingX, colorPickerDim.xLimit());
        }

        return satLightGradientDown;
    }

    public void setColorFromMouseClick(double mouseX, double mouseY) {
        if(clickedSatLightGradient(mouseX, mouseY)) {
            setSatLightFromMouse(mouseX, mouseY);
        }
        else if(clickedHueSlider(mouseX, mouseY)) {
            setHueFromMouse(mouseX);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY)) {
            mouseDown = true;
            hueSliderDown = false; //has to be called because mouseReleased doesn't always set these back
            satLightGradientDown = false;
            setColorFromMouseClick(mouseX, mouseY);
            return true;
        }
        return entryWidget.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseDown = false;
        hueSliderDown = false;
        satLightGradientDown = false;
        return false;
    }
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        //Checks if the mouse is either over the color picker or the color controller
        //The addition/subtraction of the outline and extra 3 pixels is to account for both the outline and the background
        if (mouseX >= colorPickerDim.x() - outline - 3 && mouseX <= colorPickerDim.xLimit() + outline + 3
                && mouseY >= colorPickerDim.y() - outline - 3 && mouseY <= colorPickerDim.yLimit() + outline + 3) {
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
        charTyped = true;
        return entryWidget.charTyped(chr, modifiers);
    }

    @Override
    public void setDimension(Dimension<Integer> dim) {
        super.setDimension(dim);

        int colorPickerHeight = (dim.height() * 2) + 7;
        int colorPickerX = dim.centerX() - getXPadding() * 2;

        //A single dimension for the entire color picker as a whole
        //Division is used for the main color preview, saturation/light picker, and hue slider to determine their dimensions
        colorPickerDim = Dimension.ofInt(colorPickerX, dim.y() - colorPickerHeight - sliderHeight, dim.xLimit() - colorPickerX, colorPickerHeight);
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

    public void setThumbX() {
        //Sets the thumb x for both hue and sat/light
        hueThumbX = getHueThumbX();
        satLightThumbX = getSatLightThumbX();
    }

    protected int getHueThumbX() {
        int min = colorPickerDim.x();
        int max = colorPickerDim.xLimit();
        int value = (int) (min + colorPickerDim.width() * this.hue);

        return Mth.clamp(value, min, max);
    }

    protected int getSatLightThumbX() {
        int min = colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX + 1;
        int max = colorPickerDim.xLimit();
        int value = (int) (min + (colorPickerDim.width() - ((float) colorPickerDim.xLimit() / previewPortion)) * this.saturation);

        return Mth.clamp(value, min, max);
    }

    protected int getSatLightThumbY() {
        int min = colorPickerDim.y();
        int max = colorPickerDim.yLimit() - sliderHeight - paddingY;
        int value = (int) (min + (colorPickerDim.height() - sliderHeight - paddingY) * (1.0f - this.light));

        return Mth.clamp(value, min, max);
    }

    protected int getThumbWidth() {
        return 4;
    }

    protected int getThumbHeight() {
        return 4;
    }

    public void setHueFromMouse(double mouseX) {
        //Changes the hue of the pending color based on the mouseX's pos.
        //relative to the colorPickerDim's x/xLimit
        if(mouseX < colorPickerDim.x()) {
            this.hue = 0f;
        } else if (mouseX > colorPickerDim.xLimit()) {
            this.hue = 1f;
        } else {
            float newHue = ((float) (mouseX - colorPickerDim.x()) / colorPickerDim.width());

            this.hue = Mth.clamp(newHue, 0f, 1f);
        }

        setColorControllerFromHSL();
    }

    public void setSatLightFromMouse(double mouseX, double mouseY) {
//        fillSidewaysGradient(graphics, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX + 1, colorPickerDim.y(), colorPickerDim.xLimit(), colorPickerDim.yLimit() - sliderHeight - paddingY, 3, (int) getRgbFromHue(), 0xFFFFFFFF);
        if(mouseX < colorPickerDim.x() + ((double) colorPickerDim.xLimit() / previewPortion)) {
            this.saturation = 0f;
        } else if (mouseX > colorPickerDim.xLimit()) {
            this.saturation = 1f;
        } else {
            float newSat = ((float) (mouseX - (colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion))) / (colorPickerDim.width() - ((float) colorPickerDim.xLimit() / previewPortion)));

            this.saturation = Mth.clamp(newSat, 0f, 1.0f);
        }

        if(mouseY < colorPickerDim.y()) {
            this.light = 1f;
        } else if (mouseY > colorPickerDim.yLimit() - sliderHeight - paddingY) {
            this.light = 0f;
        } else {
            float newLight = ((float) (mouseY - colorPickerDim.y()) / (colorPickerDim.height() - sliderHeight - paddingY));

            this.light = Mth.clamp(1f - newLight, 0f, 1.0f);
        }

        setColorControllerFromHSL();
    }

    public void setColorControllerFromHSL() {
        //Updates the current color controller's pending value based from HSL to RGB
        float trueHue = (float) (hueThumbX - colorPickerDim.x()) / colorPickerDim.width();
        colorController.option().requestSet(Color.getHSBColor(trueHue, saturation, light));
    }

    protected void updateHSL() {
        this.HSL = getHSL();
        this.hue = getHue();
        this.saturation = getSaturation();
        this.light = getLight();
        if(charTyped) {
            setThumbX();
            charTyped = false;
        }
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

    protected float getRgbFromHueX() {
        float trueHue = (float) (hueThumbX - colorPickerDim.x()) / colorPickerDim.width();

        return Color.HSBtoRGB(trueHue, 1, 1);
    }

    public ColorController.ColorControllerElement getEntryWidget() {
        return entryWidget;
    }
}
