package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.YACLRenderHelper;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.awt.*;

public class ColorPickerWidget extends ControllerPopupWidget<ColorController> {
    /*? if >1.20.1 {*/
    private static final ResourceLocation COLOR_PICKER_LOCATION = YACLPlatform.rl("controller/colorpicker");
    private static final ResourceLocation TRANSPARENT_TEXTURE_LOCATION = YACLPlatform.rl("controller/transparent");
    /*?} else {*/
    /*// nineslice and repeating only work on a 256x atlas
    private static final ResourceLocation COLOR_PICKER_ATLAS = YACLPlatform.rl("textures/gui/colorpicker-atlas.png");
    *//*?}*/

    private final ColorController controller;
    private final ColorController.ColorControllerElement entryWidget;
    protected MutableDimension<Integer> colorPickerDim;
    protected MutableDimension<Integer> previewColorDim;
    protected MutableDimension<Integer> saturationLightDim;
    protected MutableDimension<Integer> hueGradientDim;
    protected MutableDimension<Integer> alphaGradientDim;
    private boolean mouseDown;
    private boolean hueSliderDown;
    private boolean satLightGradientDown;
    private boolean alphaSliderDown;
    private int hueThumbX;
    private int satLightThumbX;
    private int alphaThumbX;
    private boolean charTyped;

    //The width of the outline between each color picker element(color preview, saturation/light gradient, hue gradient)
    //Note: Additional space may need to be manually made upon increasing the outline
    private final int outline = 1;

    //The main color preview's portion of the color picker as a whole
    //example: if previewPortion is set to 7, then the color preview will take up
    //a 7th of the color picker's width
    private final int previewPortion = 7;

    //The height in pixels of the hue slider
    //example: if the sliderHeight is set to 7, then the hue slider will be 7 pixels, with some extra padding between
    //the color preview and the HSL gradient to allow for an outline(determined by the "outline" int)
    private final int sliderHeight = 7;

    //The x padding between the color preview and saturation/light gradient.
    //Does NOT account for the outline on its own
    private final int paddingX = 1;

    //The y padding between the hue gradient and color preview & saturation/light gradient.
    //Does NOT account for the outline on its own
    private final int paddingY = 3;


    private float[] HSL;
    private float hue;
    private float saturation;
    private float light;
    private int alpha;

    public ColorPickerWidget(ColorController control, YACLScreen screen, Dimension<Integer> dim, ColorController.ColorControllerElement entryWidget) {
        super(control, screen, dim, entryWidget);
        this.controller = control;
        this.entryWidget = entryWidget;

        setDimension(dim);

        updateHSL();
        setThumbX();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        updateHSL();

        int thumbWidth = 4;
        int thumbHeight = 4;

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 10); // render over text

        //Background
        /*? if >1.20.3 {*/
        graphics.blitSprite(COLOR_PICKER_LOCATION, colorPickerDim.x() - 5, colorPickerDim.y() - 5, colorPickerDim.width() + 10, colorPickerDim.height() + 10);
        /*?} else {*/
        /*graphics.blitNineSliced(COLOR_PICKER_ATLAS, colorPickerDim.x() - 5, colorPickerDim.y() - 5, colorPickerDim.width() + 10, colorPickerDim.height() + 10, 3, 236, 34, 0, 0);
        *//*?}*/

        //Main color preview
        //outline
        graphics.fill(previewColorDim.x() - outline, previewColorDim.y() - outline, previewColorDim.xLimit() + outline, previewColorDim.yLimit() + outline, Color.black.getRGB());
        //transparent texture - must be rendered BEFORE the main color preview
        if(controller.allowAlpha()) {
            /*? if >1.20.3 {*/
            graphics.blitSprite(TRANSPARENT_TEXTURE_LOCATION, previewColorDim.x(), previewColorDim.y(), previewColorDim.width(), previewColorDim.height());
            /*?} else {*/
            /*graphics.blitRepeating(COLOR_PICKER_ATLAS, previewColorDim.x(), previewColorDim.y(), previewColorDim.width(), previewColorDim.height(), 236, 0, 8, 8);
            *//*?}*/
        }
        //main color preview
        graphics.fill(previewColorDim.x(), previewColorDim.y(), previewColorDim.xLimit(), previewColorDim.yLimit(), controller.option().pendingValue().getRGB());

        //Saturation/light gradient
        //outline
        graphics.fill(saturationLightDim.x() - outline, saturationLightDim.y() - outline, saturationLightDim.xLimit() + outline, saturationLightDim.yLimit() + outline, Color.black.getRGB());
        //White to pending color's RGB from hue, left to right
        fillSidewaysGradient(graphics, saturationLightDim.x(), saturationLightDim.y(), saturationLightDim.xLimit(), saturationLightDim.yLimit(), 0xFFFFFFFF, (int) getRgbFromHueX());
        //Transparent to black, top to bottom
        graphics.fillGradient(saturationLightDim.x(), saturationLightDim.y(), saturationLightDim.xLimit(), saturationLightDim.yLimit(), 0x00000000, 0xFF000000);
        //Sat/light thumb shadow
        graphics.fill(satLightThumbX - thumbWidth / 2 - 2, getSatLightThumbY() + thumbHeight / 2 + 2, satLightThumbX + thumbWidth / 2 + 1, getSatLightThumbY() - thumbHeight / 2 - 1, 0xFF404040);
        //Sat/light thumb - extra 1 pixel on left and top to make it centered
        graphics.fill(satLightThumbX - thumbWidth / 2 - 1, getSatLightThumbY() + thumbHeight / 2 + 1, satLightThumbX + thumbWidth / 2, getSatLightThumbY() - thumbHeight / 2, -1);

        //Hue gradient
        //outline
        graphics.fill(hueGradientDim.x() - outline, hueGradientDim.y() - outline, hueGradientDim.xLimit() + outline, hueGradientDim.yLimit() + outline, Color.black.getRGB());
        //Hue rainbow gradient
        drawRainbowGradient(graphics, hueGradientDim.x(), hueGradientDim.y(), hueGradientDim.xLimit(), hueGradientDim.yLimit());
        //Hue slider thumb shadow
        graphics.fill(hueThumbX - thumbWidth / 2 - 1, hueGradientDim.y() - outline - 1, hueThumbX + thumbWidth / 2 + 1, hueGradientDim.yLimit() + outline + 1, 0xFF404040);
        //Hue slider thumb
        graphics.fill(hueThumbX - thumbWidth / 2, hueGradientDim.y() - outline, hueThumbX + thumbWidth / 2, hueGradientDim.yLimit() + outline, -1);

        if(controller.allowAlpha()) {
            //outline
            graphics.fill(alphaGradientDim.x() - outline, alphaGradientDim.y() - outline, alphaGradientDim.xLimit() + outline, alphaGradientDim.yLimit() + outline, Color.black.getRGB());
            //Transparent texture
            /*? if >1.20.3 {*/
            graphics.blitSprite(TRANSPARENT_TEXTURE_LOCATION, alphaGradientDim.x(), alphaGradientDim.y(), alphaGradientDim.width(), sliderHeight);
            /*?} else {*/
            /*graphics.blitRepeating(COLOR_PICKER_ATLAS, alphaGradientDim.x(), alphaGradientDim.y(), alphaGradientDim.width(), sliderHeight, 236, 0, 8, 8);
            *//*?}*/
            //Pending color to transparent
            fillSidewaysGradient(graphics, alphaGradientDim.x(), alphaGradientDim.y(), alphaGradientDim.xLimit(), alphaGradientDim.yLimit(), getRgbWithoutAlpha(), 0x00000000);
            //Alpha slider thumb shadow
            graphics.fill(alphaThumbX - thumbWidth / 2 - 1, alphaGradientDim.y() - outline - 1, alphaThumbX + thumbWidth / 2 + 1, alphaGradientDim.yLimit() + outline + 1, 0xFF404040);
            //Alpha slider thumb
            graphics.fill(alphaThumbX - thumbWidth / 2, alphaGradientDim.y() - outline, alphaThumbX + thumbWidth / 2, alphaGradientDim.yLimit() + outline, -1);
        }

        //graphics.blitRepeating(COLOR_PICKER_ATLAS, colorPickerDim.x(), colorPickerDim.y(), colorPickerDim.width(), colorPickerDim.height(), 237, 0, 4, 4);

        graphics.pose().popPose();
    }

    public boolean clickedHueSlider(double mouseX, double mouseY) {
        if (satLightGradientDown || alphaSliderDown) return false;

        if (mouseY >= hueGradientDim.y() && mouseY <= hueGradientDim.yLimit()) {
            if (mouseX >= hueGradientDim.x() && mouseX <= hueGradientDim.xLimit()) {
                hueSliderDown = true;
            }
        }

        if (hueSliderDown) {
            hueThumbX = (int) Mth.clamp(mouseX, hueGradientDim.x(), hueGradientDim.xLimit());
        }

        return hueSliderDown;
    }

    public boolean clickedSatLightGradient(double mouseX, double mouseY) {
        if (hueSliderDown || alphaSliderDown) return false;

        if (mouseX >= saturationLightDim.x() && mouseX <= saturationLightDim.xLimit()) {
            if (mouseY >= saturationLightDim.y() && mouseY <= saturationLightDim.yLimit()) {
                satLightGradientDown = true;
            }
        }

        if(satLightGradientDown) {
            satLightThumbX = (int) Mth.clamp(mouseX, saturationLightDim.x(), saturationLightDim.xLimit());
        }

        return satLightGradientDown;
    }

    public boolean clickedAlphaSlider(double mouseX, double mouseY) {
        if (satLightGradientDown || hueSliderDown) return false;

        if (mouseX >= alphaGradientDim.x() && mouseX <= alphaGradientDim.xLimit()) {
            if (mouseY >= alphaGradientDim.y() && mouseY <= alphaGradientDim.yLimit()) {
                alphaSliderDown = true;
            }
        }

        if (alphaSliderDown) {
            alphaThumbX = (int) Mth.clamp(mouseX, alphaGradientDim.x(), alphaGradientDim.xLimit());
        }

        return alphaSliderDown;
    }

    public void setColorFromMouseClick(double mouseX, double mouseY) {
        if (clickedSatLightGradient(mouseX, mouseY)) {
            setSatLightFromMouse(mouseX, mouseY);
        } else if (clickedHueSlider(mouseX, mouseY)) {
            setHueFromMouse(mouseX);
        } else if (controller.allowAlpha() && clickedAlphaSlider(mouseX, mouseY)) {
            setAlphaFromMouse(mouseX);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            mouseDown = true;
            hueSliderDown = false;
            satLightGradientDown = false;
            alphaSliderDown = false;
            setColorFromMouseClick(mouseX, mouseY);
            return true;
        } else if (entryWidget.isMouseOver(mouseX, mouseY)) {
            return entryWidget.mouseClicked(mouseX, mouseY, button);
        } else {
            close(); //removes color picker
            return false;
        }
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        mouseDown = false;
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
        if (mouseDown || isMouseOver(mouseX, mouseY)) {
            setColorFromMouseClick(mouseX, mouseY);
            return true;
        }
        return entryWidget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
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
        int colorPickerY = dim.y() - colorPickerHeight - sliderHeight;
        int alphaSliderHeight = 0;
        if (controller.allowAlpha()) {
            alphaSliderHeight = sliderHeight + outline + paddingY;
            colorPickerHeight += alphaSliderHeight;
            colorPickerY -= alphaSliderHeight;
        }

        //Check if the color picker should be moved to beneath the controller
        //Add additional numbers after colorPickerY to reduce the "strictness" of this detection
        if (colorPickerY < screen.tabArea.top()) {
            colorPickerY = dim.yLimit() + sliderHeight;
        }

        //A single dimension for the entire color picker as a whole
        //Division is used for the main color preview, saturation/light picker, and hue slider to determine their dimensions
        colorPickerDim = Dimension.ofInt(colorPickerX, colorPickerY, dim.xLimit() - colorPickerX, colorPickerHeight);

        previewColorDim = Dimension.ofInt(colorPickerDim.x(), colorPickerDim.y(), (colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) - paddingX) - colorPickerDim.x(), (colorPickerDim.yLimit() - sliderHeight - paddingY) - colorPickerDim.y() - alphaSliderHeight);
        saturationLightDim = Dimension.ofInt(colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX + 1, colorPickerDim.y(), colorPickerDim.xLimit() - (colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX + 1), (colorPickerDim.yLimit() - sliderHeight - paddingY) - colorPickerDim.y() - alphaSliderHeight);
        hueGradientDim = Dimension.ofInt(colorPickerDim.x(), colorPickerDim.yLimit() - sliderHeight - alphaSliderHeight, colorPickerDim.width(), sliderHeight);
        if (controller.allowAlpha()) {
            alphaGradientDim = Dimension.ofInt(hueGradientDim.x(), hueGradientDim.y() + alphaSliderHeight, hueGradientDim.width(), sliderHeight);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        entryWidget.hoveredOverColorPreview = entryWidget.isMouseOverColorPreview(mouseX, mouseY);
    }

    @Override
    public void close() {
        entryWidget.removeColorPicker();
    }

    @Override
    public Component popupTitle() {
        return Component.translatable("yacl.control.color.color_picker_title");
    }

    public void setThumbX() {
        //Sets the thumb x for both hue and sat/light
        hueThumbX = getHueThumbX();
        satLightThumbX = getSatLightThumbX();
        if (controller.allowAlpha()) {
            alphaThumbX = getAlphaThumbX();
        }
    }

    protected int getHueThumbX() {
        int min = hueGradientDim.x();
        int max = hueGradientDim.xLimit();
        int value = (int) (min + hueGradientDim.width() * this.hue);

        return Mth.clamp(value, min, max);
    }

    protected int getSatLightThumbX() {
        int min = saturationLightDim.x();
        int max = saturationLightDim.xLimit();
        int value = (int) (min + (saturationLightDim.width() * this.saturation));

        return Mth.clamp(value, min, max);
    }

    protected int getSatLightThumbY() {
        int min = saturationLightDim.y();
        int max = saturationLightDim.yLimit();
        int value = (int) (min + (saturationLightDim.height() * (1.0f - this.light)));

        return Mth.clamp(value, min, max);
    }

    protected int getAlphaThumbX() {
        int min = alphaGradientDim.x();
        int max = alphaGradientDim.xLimit();
        int value = max - (alphaGradientDim.width() * this.alpha / 255);

        return Mth.clamp(value, min, max);
    }

    public void setHueFromMouse(double mouseX) {
        //Changes the hue of the pending color based on the mouseX's pos.
        //relative to the colorPickerDim's x/xLimit
        if(mouseX < hueGradientDim.x()) {
            this.hue = 0f;
        } else if (mouseX > hueGradientDim.xLimit()) {
            this.hue = 1f;
        } else {
            float newHue = (float) (mouseX - hueGradientDim.x()) / hueGradientDim.width();

            this.hue = Mth.clamp(newHue, 0f, 1f);
        }

        setColorControllerFromHSL();
    }

    public void setSatLightFromMouse(double mouseX, double mouseY) {
        if(mouseX < saturationLightDim.x()) {
            this.saturation = 0f;
        } else if (mouseX > saturationLightDim.xLimit()) {
            this.saturation = 1f;
        } else {
            float newSat = (float) (mouseX - saturationLightDim.x()) / saturationLightDim.width();

            this.saturation = Mth.clamp(newSat, 0f, 1.0f);
        }

        if(mouseY < saturationLightDim.y()) {
            this.light = 1f;
        } else if (mouseY > saturationLightDim.yLimit()) {
            this.light = 0f;
        } else {
            float newLight = (float) (mouseY - saturationLightDim.y()) / saturationLightDim.height();

            this.light = Mth.clamp(1f - newLight, 0f, 1.0f);
        }

        setColorControllerFromHSL();
    }

    public void setAlphaFromMouse(double mouseX) {
        //Changes the alpha of the pending color based on the mouseX's pos.
        if(mouseX < alphaGradientDim.x()) {
            this.alpha = 255;
        } else if (mouseX > alphaGradientDim.xLimit()) {
            this.alpha = 0;
        } else {
            int newAlpha = (int) ((mouseX - alphaGradientDim.xLimit()) / alphaGradientDim.width() * -255);

            this.alpha = Mth.clamp(newAlpha, 0, 255);
        }

        setColorControllerFromHSL();
    }

    public void setColorControllerFromHSL() {
        //Updates the current color controller's pending value based from HSL to RGB
        float trueHue = (float) (hueThumbX - colorPickerDim.x()) / colorPickerDim.width();
        Color color = Color.getHSBColor(trueHue, saturation, light);
        Color returnColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        controller.option().requestSet(returnColor);
    }

    protected void updateHSL() {
        this.HSL = getHSL();
        this.hue = hue();
        this.saturation = saturation();
        this.light = light();
        this.alpha = getAlpha();
        if(charTyped) {
            setThumbX();
            charTyped = false;
        }
    }

    protected float[] getHSL() {
        Color pendingValue = controller.option().pendingValue();
        return Color.RGBtoHSB(pendingValue.getRed(), pendingValue.getGreen(), pendingValue.getBlue(), null);
    }

    protected float hue() {
        //Gets the hue of the pending value
        return HSL[0];
    }

    protected float saturation() {
        //Gets the saturation of the pending value
        return HSL[1];
    }

    protected float light() {
        //Gets the light/brightness/value(has a few different names, all refer to the same thing) of the pending value
        return HSL[2];
    }

    protected int getAlpha() {
        return controller.option().pendingValue().getAlpha();
    }

    protected float getRgbFromHueX() {
        float trueHue = (float) (hueThumbX - colorPickerDim.x()) / colorPickerDim.width();

        return Color.HSBtoRGB(trueHue, 1, 1);
    }

    protected int getRgbWithoutAlpha() {
        Color pendingColor = controller.option().pendingValue();
        Color returnColor = new Color(pendingColor.getRed(), pendingColor.getGreen(), pendingColor.getBlue(), 255);
        return returnColor.getRGB();
    }
}
