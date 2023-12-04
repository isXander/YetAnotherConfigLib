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
    private final ColorController colorController;
    private final ColorController.ColorControllerElement entryWidget;
    private final YACLScreen screen;
    protected MutableDimension<Integer> colorPickerDim;
    protected MutableDimension<Integer> previewColorDim;
    protected MutableDimension<Integer> saturationLightDim;
    protected MutableDimension<Integer> hueGradientDim;
    private boolean mouseDown;

    //The width of the black outline for the entire color picker
    //Space is made in between various parts of the color picker using this variable
    //example: If the outline is set to 1, then a 1 pixel wide black outline is rendered around
    //the entire color picker, the main color preview, HSL gradient, and hue gradient
    private int outline = 1;

    //The x padding in pixels between both color picker parts and the outline of a color picker part
    private int paddingX = 1;

    //The y padding in pixels between both color picker parts and the outline of a color picker part
    private int paddingY = 1;

    //The main color preview's portion of the color picker as a whole
    //example: if previewPortion is set to 7, then the color preview will take up
    //a 7th of the color picker's width
    private int previewPortion = 7;

    //The height in pixels of the hue slider
    //example: if the sliderHeight is set to 7, then the hue slider will be 7 pixels, with some extra padding between
    //the color preview and the HSL gradient to allow for an outline(determined by the "outline" int)
    private int sliderHeight = 7;


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

        //Main color preview
        graphics.fill(colorPickerDim.x(), colorPickerDim.y() - sliderHeight - paddingY * 2 - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) - paddingX, colorPickerDim.yLimit(), 3, colorController.option().pendingValue().getRGB());
        //outline
        graphics.fill(colorPickerDim.x() - outline, colorPickerDim.y() - sliderHeight - paddingY * 2, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) - paddingX + outline, colorPickerDim.yLimit() - outline, 2, Color.black.getRGB());

        //HSL gradient

        //White to pending color's RGB from hue, left to right
        fillSidewaysGradient(graphics, colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight - paddingY * 2 - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX + outline, colorPickerDim.yLimit(), 3, 0xFFFFFFFF, (int) getRgbFromHue());

        //Transparent to black, top to bottom
        graphics.fillGradient(colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight - paddingY * 2 - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX + outline, colorPickerDim.yLimit(), 4,0xFF000000, 0x00000000);

        //outline
        graphics.fill(colorPickerDim.xLimit() + outline, colorPickerDim.y() - sliderHeight - paddingY * 2, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + paddingX, colorPickerDim.yLimit() - outline, 2,Color.black.getRGB());

        //Hue gradient

        //Hue rainbow gradient
        drawRainbowGradient(graphics, colorPickerDim.x(), colorPickerDim.y(), colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight, 3);

        //Hue slider thumb
        graphics.fill(getHueThumbX() - getThumbWidth() / 2, colorPickerDim.y(), getHueThumbX() + getThumbWidth() / 2, colorPickerDim.y() - sliderHeight, 5, -1);
        //Hue slider thumb shadow
        graphics.fill(getHueThumbX() - getThumbWidth() / 2 - 1, colorPickerDim.y() + 1, getHueThumbX() + getThumbWidth() / 2 + 1, colorPickerDim.y() - sliderHeight - 1, 4, 0xFF404040);

        //outline
        graphics.fill(colorPickerDim.x() - outline, colorPickerDim.y() + outline, colorPickerDim.xLimit() + outline, colorPickerDim.y() - sliderHeight - outline, 2, Color.black.getRGB());


        //Background
        //Space was added between the color preview, HSL gradient, and hue gradient earlier
//        graphics.fill(colorPickerDim.x() - outline, colorPickerDim.y() + outline, colorPickerDim.xLimit() + outline, colorPickerDim.yLimit() - outline, 1, 0xFF000000);
        drawBackground(graphics, colorPickerDim.x() - outline, colorPickerDim.y() + outline, colorPickerDim.xLimit() + outline, colorPickerDim.yLimit() - outline, 1);
    }

    public void drawBackground(GuiGraphics graphics, int x1, int y1, int x2, int y2, int z) {
        //Renders a fake container background based on the size given
        //This is done because all other container backgrounds(such as the inventory) are actually rendered using a texture

        //The light grey color used in inventory containers
        //Other colors used include: normal white and normal black
        int grey = new Color(0xC6C6C6).getRGB();

        //The center color(grey)
        graphics.fill(x1 - 1, y1 + 1, x2 + 1, y2 - 1, z, grey);
        drawRoundOutline(graphics, x1 - 1, y1 + 1, x2 + 1, y2 - 1, z, grey, 1);

        //The white outline around the grey
        drawRoundOutline(graphics, x1 - 2, y1 + 2, x2 + 2, y2 - 2, z, Color.white.getRGB(), 1);

        //The black outline around the whole thing
//        drawRoundOutline(graphics, x1 - 4, y1 + 4, x2 + 4, y2 - 4, z, Color.black.getRGB(), 2);
        drawRoundOutline(graphics, x1 - 3, y1 + 3, x2 + 3, y2 - 3, z, Color.black.getRGB(), 1);
    }

    protected void drawRoundOutline(GuiGraphics graphics, int x1, int y1, int x2, int y2, int z, int color, int pixels) {
        graphics.fill(x1, y2, x2, y2 - pixels, z, color); //top bar
        graphics.fill(x1, y1 + pixels, x2, y1, z, color); //bottom bar
        graphics.fill(x1 - pixels, y1, x1, y2, z, color); //left bar
        graphics.fill(x2, y1, x2 + pixels, y2, z, color); //right bar
        graphics.fill(x2 - pixels, y2 + pixels, x2, y2, z, color); //top right square
        graphics.fill(x1 + pixels, y2 + pixels, x1, y2, z, color); //top left square
        graphics.fill(x2 - pixels, y1 - pixels, x2, y1, z, color); //bottom right square
        graphics.fill(x1 + pixels, y1 - pixels, x1, y1, z, color); //bottom left square
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
        //FIXME - y/yLimit are flipped? - (dim.height() * 2) - 7 ???
        colorPickerDim = Dimension.ofInt(colorPickerX, dim.y(), (dim.width() + dim.x() - colorPickerX), colorPickerHeight);
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
