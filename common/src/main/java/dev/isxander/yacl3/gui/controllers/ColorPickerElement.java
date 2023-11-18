package dev.isxander.yacl3.gui.controllers;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.TooltipButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;

public class ColorPickerElement extends StringControllerElement implements ContainerEventHandler {
    private boolean mouseDown;
    private final ColorController colorController;
    private final ColorController.ColorControllerElement controllerElement;
    protected Dimension<Integer> inputFieldBounds;
    protected MutableDimension<Integer> colorPickerDim;
    private Dimension<Integer> sliderBounds;

    private final TooltipButtonWidget toggleColorPickerButton;
    private int outline = 1;

    private float[] HSL;
    private float hue;
    private float saturation;
    private float light;

    public ColorPickerElement(ColorController.ColorControllerElement element, ColorController control, YACLScreen screen, Dimension<Integer> dim) {
        super(control, screen, dim, true);
        this.colorController = control;
        this.controllerElement = element;

//        int previewSize = (dim.height() - getYPadding() * 2) / 2;
//        int buttonX = dim.xLimit() - getXPadding() - previewSize - inputFieldBounds.width() - 8;
//        int buttonY = dim.centerY() - previewSize / 2 - 2;

        toggleColorPickerButton = new TooltipButtonWidget(screen, colorPickerDim.x(), colorPickerDim.y(), colorPickerDim.width(), colorPickerDim.height(),
                Component.empty(), Component.literal("Toggle Color Picker"), btn -> {
            System.out.println("color picker toggled!");
        });

        setDimension(dim);

        this.HSL = getHSL();
        this.hue = getHue();
        this.saturation = getSaturation();
        this.light = getLight();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

//        toggleColorPickerButton.render(graphics, mouseX, mouseY, delta);

        //DELETEME
//            int hueSliderX = colorPickerDim.x() - 30 - inputFieldBounds.width() - 40;
//            int hueSliderY1 = colorPickerDim.y() + 10 - 20;
//            int hueSliderXLimit = inputFieldBounds.xLimit() + 5;
//            int hueSliderYLimit1 = colorPickerDim.yLimit() + 8 - 20;
//            graphics.fill(hueSliderX, hueSliderY1, hueSliderXLimit, hueSliderYLimit1, 10, Color.YELLOW.getRGB());
//            super.render(graphics, mouseX, mouseY, delta);

        //FIXME - If the color picker is towards the top of the category, it will appear above the color controller instead of below
        //FIXME - The color preview doesn't have enough room for the translation string

//        graphics.fill(colorPickerDim.x(), colorPickerDim.y(), colorPickerDim.xLimit(), colorPickerDim.yLimit(), Color.YELLOW.getRGB());

//        DELETEME
//            colorPickerDim.move(-inputFieldBounds.width() - 40, -20);
//            int x = colorPickerDim.x();
//            int y = colorPickerDim.y();
//            int outline = 1; //"outline" width/height offset


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

        // DELETEME
//         int gradientX = colorPickerDim.xLimit();
//         int gradientY = colorPickerDim.y() - 35;
//         int gradientXLimit = inputFieldBounds.xLimit() + 5 - outline;
//         int gradientYLimit = colorPickerDim.yLimit() + 1;

        //HSL gradient

        //White to pending color's RGB, left to right
        fillSidewaysGradient(graphics, colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + 1, colorPickerDim.yLimit(), 2, 0xFFFFFFFF, (int) getRgbFromHue());

        //Transparent to black, top to bottom
        graphics.fillGradient(colorPickerDim.xLimit(), colorPickerDim.y() - sliderHeight - outline, colorPickerDim.x() + (colorPickerDim.xLimit() / previewPortion) + 1, colorPickerDim.yLimit(), 3,0xFF000000, 0x00000000);

        //DELETEME
//        int hueSliderY = colorPickerDim.y() + 10;
//        int hueSliderYLimit = colorPickerDim.yLimit() + 8;

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


        //FIXME - It would be much more ideal for the mouseClicked override to handle this instead
        //Methods used to determine mouse clicks

        //Temporary workaround for mouseClicked not working
        //This detects when the left mouse button has been clicked anywhere on screen
        int button = GLFW.glfwGetMouseButton(this.client.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
        mouseDown = button == GLFW.GLFW_PRESS;
//            if(button == GLFW.GLFW_PRESS) {
//                System.out.println("yay");
//                //TODO - mouseDown boolean enabling/disabling
//            }

        if(mouseDown) {
            //Using the mouseClicked "override"(more like a method in this case in the meantime)
            //to make it easier for readability and for the future if the mouseClicked override is fixable
//            mouseClicked(mouseX, mouseY, 0);
            setHueFromX(mouseX);
        }
    }

    @Override
    public void setDimension(Dimension<Integer> dim) {
        super.setDimension(dim);

        int width = Math.max(6, Math.min(textRenderer.width(getValueText()), getUnshiftedLength()));


        int trackWidth = dim.width() / 3;
        if (optionNameString.isEmpty())
            trackWidth = dim.width() / 2;

//        colorPickerDim = Dimension.ofInt(dim.xLimit() - getXPadding() - colorPickerHeight, dim.centerY() - colorPickerHeight / 2, colorPickerHeight, colorPickerHeight);
        sliderBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - getThumbWidth() / 2 - trackWidth, dim.centerY() - 5, trackWidth, 10);
        inputFieldBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - width, dim.centerY() - textRenderer.lineHeight / 2, width, textRenderer.lineHeight);

        int colorPickerHeight = (dim.height() * -2) - 7;
        int colorPickerX = dim.centerX() - getXPadding() * 2;

        //A single dimension for the entire color picker as a whole
        //Would allow for the x/y(Limit) to work for the outline by adding + 1 to everything
        //Division would be used for the bigger color preview, light and saturation picker, and hue slider
        //to determine their dimensions
        //FIXME - x/y, xLimit/yLimits are seemingly flipped
        colorPickerDim = Dimension.ofInt(colorPickerX - outline, dim.y() - outline, (dim.width() + dim.x() - colorPickerX) - outline, colorPickerHeight- outline);
    }

    //SLIDER related overrides

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
//            System.out.println("e");
        this.mouseDown = mouseDown;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return ImmutableList.of(toggleColorPickerButton);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (mouseX >= colorPickerDim.x() && mouseX <= colorPickerDim.xLimit()
                && mouseY >= colorPickerDim.yLimit() && mouseY <= colorPickerDim.y()) { //y and yLimit flipped apparently?
            return true;
        }

        return false;
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
        return false;
//            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean isDragging() {
        return false;
    }

    @Override
    public void setDragging(boolean dragging) {

    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return null;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener child) {

    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {

        //Checks if the mouse is either over the color picker or the color controller
        if (mouseX >= colorPickerDim.x() && mouseX <= colorPickerDim.xLimit()
                && mouseY >= colorPickerDim.yLimit() && mouseY <= colorPickerDim.y()) { //y and yLimit flipped apparently?
            return true;
        }
        //Checks for mouse over color controller
//        return super.isMouseOver(mouseX, mouseY);
        return false;
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    protected void setHueFromMouse(double mouseX) {
//            double value = (mouseX - colorPickerDim.x() - 30) / sliderBounds.width() * (inputFieldBounds.xLimit() + 5 - colorPickerDim.x() - 30) * 255;
//            int red = ((int) value >> 16) & 255;
//            int green = ((int) value >> 8) & 255;
//            int blue = (int) value & 255;
//            int rgb = red + green + blue;
//            System.out.println("value: " + value);
//            System.out.println("rgb: " + rgb);
        System.out.println("color: " + colorController.option().pendingValue().getRGB());
//            int rgb = colorController.option().pendingValue().getRGB();
//            int red = (rgb >> 16) & 255;
//            System.out.println("red: " );
//            colorController.option().requestSet(new Color((int) value));
    }

    public void hide() {
        this.controllerElement.setColorPickerVisible(false);
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

    public void setHueFromX(int mouseX) {
        //Changes the hue of the pending color based on the mouseX's pos.
        //relative to the colorPickerDim's x/xLimit

//        Color hueChange;

        if(mouseX < colorPickerDim.x()) {
            this.hue = 0f;
//            return;
        } else if (mouseX > colorPickerDim.xLimit()) {
            this.hue = 1f;
//            return;
        } else {
//            hueChange = Color.getHSBColor(mouseX, saturation, light);
//            this.hue = 1.0f - mouseX;
//            float newHue = (mouseX - colorPickerDim.x()) / 100f * 0.9f;
//
//            System.out.println(newHue);
//            System.out.println(Mth.approachDegrees(newHue));

            float newHue = ((float) (mouseX - colorPickerDim.x()) / colorPickerDim.width());

            this.hue = Mth.clamp(newHue, 0f, 1.0f);
        }

        setColorControllerFromHSL();
//        return 1;
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
//        Color pendingValue = colorController.option().pendingValue();
//        float[] HSL = Color.RGBtoHSB(pendingValue.getRed(), pendingValue.getGreen(), pendingValue.getBlue(), null);
//        return HSL[0];
    }

    protected float getSaturation() {
        //Gets the saturation of the pending value
        return HSL[1];
//        Color pendingValue = colorController.option().pendingValue();
//        float[] HSL = Color.RGBtoHSB(pendingValue.getRed(), pendingValue.getGreen(), pendingValue.getBlue(), null);
//        return HSL[1];
    }

    protected float getLight() {
        //Gets the light/brightness/value of the pending value
        return HSL[2];
//        Color pendingValue = colorController.option().pendingValue();
//        float[] HSL = Color.RGBtoHSB(pendingValue.getRed(), pendingValue.getGreen(), pendingValue.getBlue(), null);
//        return HSL[2];
    }

    protected float getRgbFromHue() {
        return Color.HSBtoRGB(hue, 1, 1);
    }

    protected int getThumbWidth() {
        return 4;
    }
}
