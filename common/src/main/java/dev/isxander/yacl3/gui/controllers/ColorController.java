package dev.isxander.yacl3.gui.controllers;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MathUtil;

import javax.swing.plaf.basic.BasicListUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.MathContext;
import java.util.List;
import java.util.Random;

/**
 * A color controller that uses a hex color field as input.
 */
public class ColorController implements IStringController<Color> {
    private final Option<Color> option;
    private final boolean allowAlpha;

    /**
     * Constructs a color controller with {@link ColorController#allowAlpha()} defaulting to false
     *
     * @param option bound option
     */
    public ColorController(Option<Color> option) {
        this(option, false);
    }

    /**
     * Constructs a color controller
     *
     * @param option bound option
     * @param allowAlpha allows the color input to accept alpha values
     */
    public ColorController(Option<Color> option, boolean allowAlpha) {
        this.option = option;
        this.allowAlpha = allowAlpha;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<Color> option() {
        return option;
    }

    public boolean allowAlpha() {
        return allowAlpha;
    }

    @Override
    public String getString() {
        return formatValue().getString();
    }

    @Override
    public Component formatValue() {
        MutableComponent text = Component.literal("#");
        text.append(Component.literal(toHex(option().pendingValue().getRed())).withStyle(ChatFormatting.RED));
        text.append(Component.literal(toHex(option().pendingValue().getGreen())).withStyle(ChatFormatting.GREEN));
        text.append(Component.literal(toHex(option().pendingValue().getBlue())).withStyle(ChatFormatting.BLUE));
        if (allowAlpha()) text.append(toHex(option().pendingValue().getAlpha()));
        return text;
    }

    private String toHex(int value) {
        String hex = Integer.toString(value, 16).toUpperCase();
        if (hex.length() == 1)
            hex = "0" + hex;
        return hex;
    }

    @Override
    public void setFromString(String value) {
        if (value.startsWith("#"))
            value = value.substring(1);

        int red = Integer.parseInt(value.substring(0, 2), 16);
        int green = Integer.parseInt(value.substring(2, 4), 16);
        int blue = Integer.parseInt(value.substring(4, 6), 16);

        if (allowAlpha()) {
            int alpha = Integer.parseInt(value.substring(6, 8), 16);
            option().requestSet(new Color(red, green, blue, alpha));
        } else {
            option().requestSet(new Color(red, green, blue));
        }
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new ColorControllerElement(this, screen, widgetDimension);
    }

    public static class ColorControllerElement extends StringControllerElement {
        private final ColorController colorController;

        protected MutableDimension<Integer> colorPreviewDim;
        private final List<Character> allowedChars;
        private boolean mouseDown = false;
        private boolean colorPickerVisible = false;
        private boolean hovered = false;

        private ColorPickerElement colorPickerElement;

        public ColorControllerElement(ColorController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim, true);
            this.colorController = control;
            this.allowedChars = ImmutableList.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');
        }

        @Override
        protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            hovered = isMouseOver(mouseX, mouseY);
            //Replace with drawHoveredControl???
            if (isHovered()) {
                colorPreviewDim.move(-inputFieldBounds.width() - 8, -2);
                colorPreviewDim.expand(4, 4);
                super.drawValueText(graphics, mouseX, mouseY, delta);
            } else {
                colorPickerVisible = false;
                colorPickerElement = null;
            }

            graphics.fill(colorPreviewDim.x(), colorPreviewDim.y(), colorPreviewDim.xLimit(), colorPreviewDim.yLimit(), colorController.option().pendingValue().getRGB());
            drawOutline(graphics, colorPreviewDim.x(), colorPreviewDim.y(), colorPreviewDim.xLimit(), colorPreviewDim.yLimit(), 1, 0xFF000000);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            super.render(graphics, mouseX, mouseY, delta);

            if(colorPickerVisible) {
                colorPickerElement = new ColorPickerElement(this, colorController, screen, getDimension());

                //FIXME - It would be much more ideal for the mouseClicked override in the color picker widget to handle this instead
                //I couldn't get the mouseClicked override for the color picker widget to work
                //The breakpoint I set for it would never activate no matter what
                //I believe this is something I've done wrong, but I couldn't figure it out
                colorPickerElement.setMouseDown(mouseDown);

                //Renders the color picker
                colorPickerElement.render(graphics, mouseX, mouseY, delta);
            } else if (!colorPickerVisible) {
                colorPickerElement = null;
            }
        }

        @Override
        public void write(String string) {
            if (string.startsWith("0x")) string = string.substring(2);
            for (char chr : string.toCharArray()) {
                if (!allowedChars.contains(Character.toLowerCase(chr))) {
                    return;
                }
            }

            if (caretPos == 0)
                return;

            String trimmed = string.substring(0, Math.min(inputField.length() - caretPos, string.length()));

            if (modifyInput(builder -> builder.replace(caretPos, caretPos + trimmed.length(), trimmed))) {
                caretPos += trimmed.length();
                setSelectionLength();
                updateControl();
            }
        }

        //STRING related overrides

        @Override
        protected void doBackspace() {
            if (caretPos > 1) {
                if (modifyInput(builder -> builder.setCharAt(caretPos - 1, '0'))) {
                    caretPos--;
                    updateControl();
                }
            }
        }

        @Override
        protected void doDelete() {
            if (caretPos >= 1) {
                if (modifyInput(builder -> builder.setCharAt(caretPos, '0'))) {
                    updateControl();
                }
            }
        }

        @Override
        protected boolean doCut() {
            return false;
        }

        @Override
        protected boolean doCopy() {
            return false;
        }

        @Override
        protected boolean doSelectAll() {
            return false;
        }

        protected void setSelectionLength() {
            selectionLength = caretPos < inputField.length() && caretPos > 0 ? 1 : 0;
        }

        @Override
        protected int getDefaultCaretPos() {
            return colorController.allowAlpha() ? 3 : 1;
        }

        @Override
        public void setDimension(Dimension<Integer> dim) {
            super.setDimension(dim);

            int previewSize = (dim.height() - getYPadding() * 2) / 2;
            colorPreviewDim = Dimension.ofInt(dim.xLimit() - getXPadding() - previewSize, dim.centerY() - previewSize / 2, previewSize, previewSize);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            int prevSelectionLength = selectionLength;
            selectionLength = 0;
            if (super.keyPressed(keyCode, scanCode, modifiers)) {
                caretPos = Math.max(1, caretPos);
                setSelectionLength();
                return true;
            } else selectionLength = prevSelectionLength;
            return false;
        }

        //MOUSE related overrides

        @Override
        public boolean isHovered() {
            if(!super.isHovered() || focused || !inputFieldFocused) {
                //Hides the color picker when option is no longer selected
//                colorPickerVisible = false;
            }
//            int x = colorPreviewDim.x() - inputFieldBounds.width() - 70;
//            int y = colorPreviewDim.y() - 55;
//            int xLimit = inputFieldBounds.xLimit() + 5;
//            int yLimit = colorPreviewDim.yLimit();

//            if(colorPickerVisible && mouseX >= x && mouseX <= xLimit && mouseY >= y && mouseY <= yLimit) {
////                mouseDown = true;
//                return true;
//            }

            return hovered || inputFieldFocused;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (super.mouseClicked(mouseX, mouseY, button)) {
                //TODO - Controller support?
                //FIXME - Clicking another category whilst the color picker is visible keeps the color picker visible when returning
                //Detects if the user has clicked the color preview
                if(clickedColorPreview(mouseX, mouseY, button)) {
                        colorPickerVisible = !colorPickerVisible;
                        playDownSound();
                }
                caretPos = Math.max(1, caretPos);
                setSelectionLength();
                mouseDown = true;
                return true;
            }

            return false;
        }

        public boolean clickedColorPreview(double mouseX, double mouseY, int button) {
            if((mouseX >= colorPreviewDim.x() && mouseX <= colorPreviewDim.xLimit())
                    && (mouseY >= colorPreviewDim.y() && mouseY <= colorPreviewDim.yLimit()) && button == 0) {
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if(isAvailable() || !mouseDown)
                return false;

            return true;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
//            if (isAvailable() && mouseDown)
//                playDownSound();
            mouseDown = false;

            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            int x = colorPreviewDim.x() - inputFieldBounds.width() - 70;
            int y = colorPreviewDim.y() - 55;
            int xLimit = inputFieldBounds.xLimit() + 5;
            int yLimit = colorPreviewDim.yLimit();

            //This is used to determine if the user has clicked within the color picker's boundaries or not
            //Should either of these variables be removed, clicking on the color picker will cause it to disappear
            if(colorPickerVisible && mouseX >= x && mouseX <= xLimit && mouseY >= y && mouseY <= yLimit) {
                inputFieldFocused = true;
                return true;
            }
            //FIXME - Color picker "z fighting" options it is above is causing issues closing the color picker
            //FIXME - Technically, the z fighting is still an issue, as the option behind it becomes selected
            //FIXME - Example: Clicking on the string controller behind the color picker, then typing types into the controller
            return super.isMouseOver(mouseX, mouseY);
        }

        @Override
        public void unfocus() {
//            colorPickerVisible = false;
            super.unfocus();
        }

        public ColorPickerElement getColorPickerElement() {
            return colorPickerElement;
        }

        public boolean isColorPickerVisible() {
            return colorPickerVisible;
        }

        public void setColorPickerVisible(boolean colorPickerVisible) {
            this.colorPickerVisible = colorPickerVisible;
        }
    }

//    public static class ColorPickerElement extends StringControllerElement {
//        private boolean mouseDown;
//        private final ColorController colorController;
//        protected Dimension<Integer> inputFieldBounds;
//        protected MutableDimension<Integer> colorPickerDim;
//        private Dimension<Integer> sliderBounds;
//
//        public ColorPickerElement(ColorController control, YACLScreen screen, Dimension<Integer> dim) {
//            super(control, screen, dim, true);
//            this.colorController = control;
//            setDimension(dim);
//        }
//
//        @Override
//        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
////            int hueSliderX = colorPickerDim.x() - 30 - inputFieldBounds.width() - 40;
////            int hueSliderY1 = colorPickerDim.y() + 10 - 20;
////            int hueSliderXLimit = inputFieldBounds.xLimit() + 5;
////            int hueSliderYLimit1 = colorPickerDim.yLimit() + 8 - 20;
////            graphics.fill(hueSliderX, hueSliderY1, hueSliderXLimit, hueSliderYLimit1, 10, Color.YELLOW.getRGB());
////            super.render(graphics, mouseX, mouseY, delta);
//
//            //FIXME - If the color picker is towards the top of the category, it will appear above the color controller instead of below
//            //FIXME - The color preview doesn't have enough room for the translation string
//            colorPickerDim.move(-inputFieldBounds.width() - 40, -20);
//            int x = colorPickerDim.x() - 30;
//            int y = colorPickerDim.y() - 35;
//            int outline = 1; //"outline" width/height offset
//
//            //Main color preview
//            //A single pixel is subtracted to allow for the "outline"
//            //The outline is really just a big black box behind the other drawn items
//            graphics.fill(x, y, colorPickerDim.xLimit() - outline, colorPickerDim.yLimit() + 1, 2, colorController.option().pendingValue().getRGB());
//
//            //HSL gradient
//            int gradientX = colorPickerDim.xLimit();
//            int gradientY = colorPickerDim.y() - 35;
//            int gradientXLimit = inputFieldBounds.xLimit() + 5 - outline;
//            int gradientYLimit = colorPickerDim.yLimit() + 1;
//
//            //White to pending color value, left to right
//            float hue = getHue();
//            float RGB = Color.HSBtoRGB(hue, 1, 1);
//
//            fillSidewaysGradient(graphics, gradientX, gradientY, gradientXLimit, gradientYLimit, 2, 0xFFFFFFFF, (int) RGB);
//
//            //Transparent to black, top to bottom
//            graphics.fillGradient(gradientX, gradientY, gradientXLimit, gradientYLimit, 3,0x00000000, 0xFF000000);
//
//            //Hue slider
//            int hueSliderY = colorPickerDim.y() + 10;
//            int hueSliderYLimit = colorPickerDim.yLimit() + 8;
//            drawRainbowGradient(graphics, x, hueSliderY, gradientXLimit, hueSliderYLimit, 2);
//            //Slider thumb
//            graphics.fill(getThumbX() - getThumbWidth() / 2, hueSliderY, getThumbX() + getThumbWidth() / 2, hueSliderYLimit, 5, -1);
//            //Slider thumb shadow
//            graphics.fill(getThumbX() - getThumbWidth() / 2 - 1, hueSliderY - 1, getThumbX() + getThumbWidth() / 2 + 1, hueSliderYLimit + 1, 4, 0xFF404040);
//
//
//            //Outline
//            //Simply draws a huge black box
//            //Space was added between the main color preview and the gradient
//            graphics.fill(x - outline, y - outline, gradientXLimit + outline, hueSliderYLimit + outline, 1, 0xFF000000);
//
//
//            //FIXME - It would be much more ideal for the mouseClicked override to handle this instead
//            //Methods used to determine mouse clicks
//
//            //Temporary workaround for mouseClicked not working
//            //This detects when the left mouse button has been clicked anywhere on screen
//            int button = GLFW.glfwGetMouseButton(this.client.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT);
//            mouseDown = button == GLFW.GLFW_PRESS;
////            if(button == GLFW.GLFW_PRESS) {
////                System.out.println("yay");
////                //TODO - mouseDown boolean enabling/disabling
////            }
//
//            if(mouseDown) {
//                //Using the mouseClicked "override"(more like a method in this case in the meantime)
//                //to make it easier for readability and for the future if the mouseClicked override is fixable
//                mouseClicked(mouseX, mouseY, 0);
//            }
//        }
//
//        @Override
//        public void setDimension(Dimension<Integer> dim) {
//            super.setDimension(dim);
//
//            int width = Math.max(6, Math.min(textRenderer.width(getValueText()), getUnshiftedLength()));
//            int previewSize = (dim.height() - getYPadding() * 2) / 2;
//            int trackWidth = dim.width() / 3;
//            if (optionNameString.isEmpty())
//                trackWidth = dim.width() / 2;
//
//            colorPickerDim = Dimension.ofInt(dim.xLimit() - getXPadding() - previewSize, dim.centerY() - previewSize / 2, previewSize, previewSize);
//            sliderBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - getThumbWidth() / 2 - trackWidth, dim.centerY() - 5, trackWidth, 10);
//            inputFieldBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - width, dim.centerY() - textRenderer.lineHeight / 2, width, textRenderer.lineHeight);
//        }
//
//        //SLIDER related overrides
//
//        public int getUnshiftedLength() {
//            if(control.option().name().getString().isEmpty())
//                return getDimension().width() - getXPadding() * 2;
//            return getDimension().width() / 8 * 5;
//        }
//
//        @Override
//        protected int getHoveredControlWidth() {
////            return sliderBounds.width() + getUnhoveredControlWidth() + 6 + getThumbWidth() / 2;
//            return Math.min(textRenderer.width(control.formatValue()), getUnshiftedLength());
//        }
//
//        @Override
//        protected int getUnhoveredControlWidth() {
//            return textRenderer.width(getValueText());
//        }
//
//        public void setMouseDown(boolean mouseDown) {
////            System.out.println("e");
//            this.mouseDown = mouseDown;
//        }
//
//        @Override
//        public boolean mouseClicked(double mouseX, double mouseY, int button) {
//            if(mouseDown) {
//                //TODO - Replace all variables like this with private vars?
//                int hueSliderX = colorPickerDim.x() - 30 - inputFieldBounds.width() - 40;
//                int hueSliderY = colorPickerDim.y() + 10;
//                int hueSliderXLimit = inputFieldBounds.xLimit() + 5;
//                int hueSliderYLimit = colorPickerDim.yLimit() + 8;
////                System.out.println("button: " +  button);
////                System.out.println("x: " + mouseX + "y: " + mouseY);
////                System.out.println("x: " + hueSliderX + "y:" + hueSliderY);
////                System.out.println("xLimit: " + hueSliderXLimit + "yLimit: " + hueSliderYLimit);
//
//                //Detects if the user has clicked the hue slider
//                if((mouseX >= hueSliderX && mouseX <= hueSliderXLimit)
//                        && (mouseY >= hueSliderY && mouseY <= hueSliderYLimit)) {
//                    System.out.println("yay");
//                    setHueFromMouse(mouseX);
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        @Override
//        public boolean mouseReleased(double mouseX, double mouseY, int button) {
//            System.out.println("yay2");
//            mouseDown = false;
//            return false;
////            return super.mouseReleased(mouseX, mouseY, button);
//        }
//
//        @Override
//        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
//            System.out.println("yay3");
//            return false;
////            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
//        }
//
//        @Override
//        public boolean isMouseOver(double mouseX, double mouseY) {
//            return super.isMouseOver(mouseX, mouseY);
//        }
//
//        @Override
//        public boolean isHovered() {
//            return super.isHovered();
//        }
//
//        @Override
//        public boolean isFocused() {
//            return true;
//        }
//
//        protected void setHueFromMouse(double mouseX) {
////            double value = (mouseX - colorPickerDim.x() - 30) / sliderBounds.width() * (inputFieldBounds.xLimit() + 5 - colorPickerDim.x() - 30) * 255;
////            int red = ((int) value >> 16) & 255;
////            int green = ((int) value >> 8) & 255;
////            int blue = (int) value & 255;
////            int rgb = red + green + blue;
////            System.out.println("value: " + value);
////            System.out.println("rgb: " + rgb);
//            System.out.println("color: " + colorController.option().pendingValue().getRGB());
////            int rgb = colorController.option().pendingValue().getRGB();
////            int red = (rgb >> 16) & 255;
////            System.out.println("red: " );
////            colorController.option().requestSet(new Color((int) value));
//        }
//
//        protected int getThumbX() {
//            //Calculates the thumb x based upon the pending value's hue
//            //Multiplying the adjustment by 1.9 instead of 2 seemed to give better results
//            double multiplyValue = 1.9;
//            int adjustmentValue = (int) ((inputFieldBounds.xLimit() + 5 - colorPickerDim.x() - 30) * getHue() * multiplyValue);
//
//            return Mth.clamp(colorPickerDim.x() - 30 + adjustmentValue, colorPickerDim.x() - 30, inputFieldBounds.xLimit() + 5);
//        }
//
//        protected float getHue() {
//            //Gets the hue of the pending value
//            Color pendingValue = colorController.option().pendingValue();
//            float[] HSL = Color.RGBtoHSB(pendingValue.getRed(), pendingValue.getGreen(), pendingValue.getBlue(), null);
//            return HSL[0];
//        }
//
//        protected int getThumbWidth() {
//            return 4;
//        }
//    }
}
