package dev.isxander.yacl3.gui.controllers;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.render.ColorGradientRenderState;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class ColorPickerWidget extends ControllerPopupWidget<ColorController> {
    public static final Identifier COLOR_PICKER_SPRITE = YACLPlatform.rl("controller/colorpicker");
    public static final Identifier TRANSPARENT_SPRITE = YACLPlatform.rl("controller/transparent");

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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        updateHSL();

        int thumbWidth = 4;
        int thumbHeight = 4;

        graphics.pose().pushMatrix();

        //Background
        int x1 = colorPickerDim.x() - 5;
        int y1 = colorPickerDim.y() - 5;
        int width1 = colorPickerDim.width() + 10;
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                COLOR_PICKER_SPRITE,
                x1, y1,
                width1, colorPickerDim.height() + 10
        );

        //Main color preview
        //outline
        graphics.fill(previewColorDim.x() - outline, previewColorDim.y() - outline, previewColorDim.xLimit() + outline, previewColorDim.yLimit() + outline, Color.black.getRGB());
        //transparent texture - must be rendered BEFORE the main color preview
        if(controller.allowAlpha()) {
            int x = previewColorDim.x();
            int y = previewColorDim.y();
            int width = previewColorDim.width();
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    TRANSPARENT_SPRITE,
                    x, y,
                    width, previewColorDim.height()
            );
        }
        //main color preview
        graphics.fill(previewColorDim.x(), previewColorDim.y(), previewColorDim.xLimit(), previewColorDim.yLimit(), controller.option().pendingValue().getRGB());

        //Saturation/light gradient
        //outline
        graphics.fill(saturationLightDim.x() - outline, saturationLightDim.y() - outline, saturationLightDim.xLimit() + outline, saturationLightDim.yLimit() + outline, Color.black.getRGB());
        //White to pending color's RGB from hue, left to right
        ColorGradientRenderState.createHorizontal(
                graphics,
                saturationLightDim.x(), saturationLightDim.y(),
                saturationLightDim.xLimit(), saturationLightDim.yLimit(),
                0xFFFFFFFF, ((int) getRgbFromHueX() & 0x00FFFFFF) | 0xFF000000
        ).submit(graphics);
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

        if (controller.allowAlpha()) {
            //outline
            graphics.fill(alphaGradientDim.x() - outline, alphaGradientDim.y() - outline, alphaGradientDim.xLimit() + outline, alphaGradientDim.yLimit() + outline, Color.black.getRGB());
            //Transparent texture
            int x = alphaGradientDim.x();
            int y = alphaGradientDim.y();
            int width = alphaGradientDim.width();
            graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    TRANSPARENT_SPRITE,
                    x, y,
                    width, sliderHeight
            );

            //Pending color to transparent
            ColorGradientRenderState.createHorizontal(
                    graphics,
                    alphaGradientDim.x(), alphaGradientDim.y(),
                    alphaGradientDim.xLimit(), alphaGradientDim.yLimit(),
                    (getRgbWithoutAlpha() & 0x00FFFFFF) | 0xFF000000, 0x00000000
            ).submit(graphics);
            //Alpha slider thumb shadow
            graphics.fill(alphaThumbX - thumbWidth / 2 - 1, alphaGradientDim.y() - outline - 1, alphaThumbX + thumbWidth / 2 + 1, alphaGradientDim.yLimit() + outline + 1, 0xFF404040);
            //Alpha slider thumb
            graphics.fill(alphaThumbX - thumbWidth / 2, alphaGradientDim.y() - outline, alphaThumbX + thumbWidth / 2, alphaGradientDim.yLimit() + outline, -1);
        }

        graphics.pose().popMatrix();

        if (isHoveringHueSlider(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.RESIZE_EW);
        } else if (isHoveringAlphaSlider(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.RESIZE_EW);
        } else if (isHoveringSatLightGradient(mouseX, mouseY)) {
            graphics.requestCursor(CursorTypes.CROSSHAIR);
        }
    }

    private boolean isHoveringHueSlider(double mouseX, double mouseY) {
        return mouseY >= hueGradientDim.y() && mouseY <= hueGradientDim.yLimit()
                && mouseX >= hueGradientDim.x() && mouseX <= hueGradientDim.xLimit();
    }

    public boolean clickedHueSlider(double mouseX, double mouseY) {
        if (satLightGradientDown || alphaSliderDown) return false;

        if (this.isHoveringHueSlider(mouseX, mouseY)) {
            hueSliderDown = true;
        }

        if (hueSliderDown) {
            hueThumbX = (int) Mth.clamp(mouseX, hueGradientDim.x(), hueGradientDim.xLimit());
        }

        return hueSliderDown;
    }

    private boolean isHoveringSatLightGradient(double mouseX, double mouseY) {
        return mouseY >= saturationLightDim.y() && mouseY <= saturationLightDim.yLimit()
                && mouseX >= saturationLightDim.x() && mouseX <= saturationLightDim.xLimit();
    }

    public boolean clickedSatLightGradient(double mouseX, double mouseY) {
        if (hueSliderDown || alphaSliderDown) return false;

        if (isHoveringSatLightGradient(mouseX, mouseY)) {
            satLightGradientDown = true;
        }

        if (satLightGradientDown) {
            satLightThumbX = (int) Mth.clamp(mouseX, saturationLightDim.x(), saturationLightDim.xLimit());
        }

        return satLightGradientDown;
    }

    private boolean isHoveringAlphaSlider(double mouseX, double mouseY) {
        if (alphaGradientDim == null) return false;
        return mouseY >= alphaGradientDim.y() && mouseY <= alphaGradientDim.yLimit()
                && mouseX >= alphaGradientDim.x() && mouseX <= alphaGradientDim.xLimit();
    }

    public boolean clickedAlphaSlider(double mouseX, double mouseY) {
        if (satLightGradientDown || hueSliderDown) return false;

        if (isHoveringAlphaSlider(mouseX, mouseY)) {
            alphaSliderDown = true;
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
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (isMouseOver(event.x(), event.y())) {
            mouseDown = true;
            hueSliderDown = false;
            satLightGradientDown = false;
            alphaSliderDown = false;
            setColorFromMouseClick(event.x(), event.y());
            return true;
        } else if (entryWidget.isMouseOver(event.x(), event.y())) {
            return entryWidget.mouseClicked(event, doubleClick);
        } else {
            close(); //removes color picker
            return false;
        }
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        mouseDown = false;
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        //Checks if the mouse is either over the color picker or the color controller
        //The addition/subtraction of the outline and extra 3 pixels is to account for both the outline and the background
        return mouseX >= colorPickerDim.x() - outline - 3 && mouseX <= colorPickerDim.xLimit() + outline + 3
                && mouseY >= colorPickerDim.y() - outline - 3 && mouseY <= colorPickerDim.yLimit() + outline + 3;
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double dx, double dy) {
        if (mouseDown || isMouseOver(event.x(), event.y())) {
            setColorFromMouseClick(event.x(), event.y());
            return true;
        }
        return entryWidget.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean charTyped(@NonNull CharacterEvent event) {
        // Done to allow for typing whilst the color picker is visible
        charTyped = true;
        return entryWidget.charTyped(event);
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
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
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
