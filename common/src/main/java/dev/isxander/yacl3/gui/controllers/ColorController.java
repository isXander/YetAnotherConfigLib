package dev.isxander.yacl3.gui.controllers;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.slider.ISliderController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.List;

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
        protected MutableDimension<Integer> colorPickerDim;
        private Dimension<Integer> sliderBounds;

        private final List<Character> allowedChars;
        private boolean mouseDown = false;
        private boolean colorPickerVisible = false;
        private float interpolation;

        public ColorControllerElement(ColorController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim, true);
            this.colorController = control;
            this.allowedChars = ImmutableList.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            super.render(graphics, mouseX, mouseY, delta);

            calculateInterpolation();
        }

        @Override
        protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            //Replace with drawHoveredControl???
            if (isHovered()) {
                colorPreviewDim.move(-inputFieldBounds.width() - 8, -2);
                colorPreviewDim.expand(4, 4);
                if(colorPickerVisible) {
                    //FIXME - If the color picker is towards the top of the category, it will appear above the color controller instead of below
                    //FIXME - The color preview doesn't have enough room for the translation string
                    colorPickerDim.move(-inputFieldBounds.width() - 40, -20);
                    int x = colorPickerDim.x() - 30;
                    int y = colorPickerDim.y() - 35;
                    int outline = 1; //"outline" width/height offset

                    //Main color preview
                    //A single pixel is subtracted to allow for the "outline"
                    //The outline is really just a big black box behind the other drawn items
                    graphics.fill(x, y, colorPickerDim.xLimit() - outline, colorPickerDim.yLimit() + 1, 2, colorController.option().pendingValue().getRGB());

                    //HSL gradient
                    int gradientX = colorPickerDim.xLimit();
                    int gradientY = colorPickerDim.y() - 35;
                    int gradientXLimit = inputFieldBounds.xLimit() + 5 - outline;
                    int gradientYLimit = colorPickerDim.yLimit() + 1;

                    //White to pending color value, left to right
                    //Gets the hue of the pending value
                    Color pendingValue = colorController.option().pendingValue();
                    float[] HSL = Color.RGBtoHSB(pendingValue.getRed(), pendingValue.getGreen(), pendingValue.getBlue(), null);
                    float hue = HSL[0];
                    float RGB = Color.HSBtoRGB(hue, 1, 1);

                    fillSidewaysGradient(graphics, gradientX, gradientY, gradientXLimit, gradientYLimit, 2, 0xFFFFFFFF, (int) RGB);

                    //Transparent to black, top to bottom
                    graphics.fillGradient(gradientX, gradientY, gradientXLimit, gradientYLimit, 3,0x00000000, 0xFF000000);

                    //Rainbow gradient
                    int rainbowY = colorPickerDim.y() + 10;
                    int rainbowYLimit = colorPickerDim.yLimit() + 8;
                    drawRainbowGradient(graphics, x, rainbowY, gradientXLimit, rainbowYLimit, 2);

                    //RGB Slider
                    //TODO - Actually work on this
                    //Square
                    graphics.fill(getThumbX() - getThumbWidth() / 2 + 1, sliderBounds.y() + 1, getThumbX() + getThumbWidth() / 2 + 1, sliderBounds.yLimit() + 1, 0xFF404040);
                    //Square shadow
                    graphics.fill(getThumbX() - getThumbWidth() / 2, sliderBounds.y(), getThumbX() + getThumbWidth() / 2, sliderBounds.yLimit(), -1);


                    //Outline
                    //Simply draws a huge black box
                    //Space was added between the main color preview and the gradient
                    graphics.fill(x - outline, y - outline, gradientXLimit + outline, rainbowYLimit + outline, 1, 0xFF000000);
                }
                super.drawValueText(graphics, mouseX, mouseY, delta);
            }

            graphics.fill(colorPreviewDim.x(), colorPreviewDim.y(), colorPreviewDim.xLimit(), colorPreviewDim.yLimit(), colorController.option().pendingValue().getRGB());
            drawOutline(graphics, colorPreviewDim.x(), colorPreviewDim.y(), colorPreviewDim.xLimit(), colorPreviewDim.yLimit(), 1, 0xFF000000);
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

            int trackWidth = dim.width() / 3;
            if (optionNameString.isEmpty())
                trackWidth = dim.width() / 2;

            int previewSize = (dim.height() - getYPadding() * 2) / 2;
            colorPreviewDim = Dimension.ofInt(dim.xLimit() - getXPadding() - previewSize, dim.centerY() - previewSize / 2, previewSize, previewSize);
            colorPickerDim = Dimension.ofInt(dim.xLimit() - getXPadding() - previewSize, dim.centerY() - previewSize / 2, previewSize, previewSize);
            sliderBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - getThumbWidth() / 2 - trackWidth, dim.centerY() - 5, trackWidth, 10);
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
            if(!super.isHovered() || focused) {
                //Hides the color picker when option is no longer selected
                colorPickerVisible = false;
            }
            return super.isHovered() || inputFieldFocused;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (super.mouseClicked(mouseX, mouseY, button)) {

                //TODO - BUTTON instead of mouseX/mouseY detection for controller support
                //FIXME - Clicking another category whilst the color picker is visible keeps the color picker visible when returning
                //Detects if the user has clicked the color preview
                if((mouseX >= colorPreviewDim.x() && mouseX <= colorPreviewDim.xLimit())
                        && (mouseY >= colorPreviewDim.y() && mouseY <= colorPreviewDim.yLimit())) {
                    colorPickerVisible = !colorPickerVisible;
                }

                caretPos = Math.max(1, caretPos);
                setSelectionLength();
                mouseDown = true;
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
            if (isAvailable() && mouseDown)
                playDownSound();
            mouseDown = false;

            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            int i = colorPickerDim.x() - inputFieldBounds.width() - 70;
            int i1 = colorPickerDim.y() - 55;
            int i2 = inputFieldBounds.xLimit() + 5;
            int i3 = colorPickerDim.yLimit();
            if(colorPickerVisible && mouseX >= i && mouseX <= i2 && mouseY >= i1 && mouseY <= i3) {
                mouseDown = true;
            }
            //FIXME - Color picker "z fighting" options it is above is causing issues closing the color picker
            return super.isMouseOver(mouseX, mouseY) || mouseDown || focused;
        }

        //SLIDER related overrides

        //TODO - Probably do want to separate the color picker from the color controller
        //TODO - These overrides specifically could cause a lot of problems

        @Override
        protected int getHoveredControlWidth() {
            return sliderBounds.width() + getUnhoveredControlWidth() + 6 + getThumbWidth() / 2;
        }

        @Override
        protected int getUnhoveredControlWidth() {
            return textRenderer.width(getValueText());
        }

        protected void calculateInterpolation() {
            int max = 255;
            int min = 1;
            int value = 100;
            interpolation = Mth.clamp((float) ((value - min) * 1 / max - min), 0f, 1f);
        }

        protected int getThumbX() {
            return (int) (sliderBounds.x() + sliderBounds.width() * interpolation);
        }

        protected int getThumbWidth() {
            return 4;
        }
    }
}
