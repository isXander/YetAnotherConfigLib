package dev.isxander.yacl3.gui.controllers;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TooltipButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

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
        private final YACLScreen screen;
        private ColorPickerElement colorPickerElement;

        protected MutableDimension<Integer> colorPreviewDim;
        private final List<Character> allowedChars;
        private boolean mouseDown = false;
        private boolean colorPickerVisible = false;
        private boolean hovered = false;

        public ColorControllerElement(ColorController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim, true);
            this.colorController = control;
            this.screen = screen;
            this.allowedChars = ImmutableList.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');
        }

        @Override
        protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            hovered = isMouseOver(mouseX, mouseY);
            if (isHovered()) {
                colorPreviewDim.move(-inputFieldBounds.width() - 8, -2);
                colorPreviewDim.expand(4, 4);
                super.drawValueText(graphics, mouseX, mouseY, delta);
            }

            graphics.fill(colorPreviewDim.x(), colorPreviewDim.y(), colorPreviewDim.xLimit(), colorPreviewDim.yLimit(), colorController.option().pendingValue().getRGB());
            drawOutline(graphics, colorPreviewDim.x(), colorPreviewDim.y(), colorPreviewDim.xLimit(), colorPreviewDim.yLimit(), 1, isMouseOverColorPreview(mouseX, mouseY) ? 0xFFFFFFFF : 0xFF000000);
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            super.render(graphics, mouseX, mouseY, delta);
            if(colorPickerVisible)
                colorPickerElement.setDimension(getDimension());
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
//            if(!super.isHovered() || focused || !inputFieldFocused) {
                //Hides the color picker when option is no longer selected
//                colorPickerVisible = false;
//            }

            return hovered || inputFieldFocused;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (super.mouseClicked(mouseX, mouseY, button)) {
                //TODO - Controller support/keyboard only support(enter key to "select" hue/saturation&value areas?)
                //Detects if the user has clicked the color preview
                if(clickedColorPreview(mouseX, mouseY)) {
                        playDownSound();
                        createOrRemoveColorPicker();
                }
                caretPos = Math.max(1, caretPos);
                setSelectionLength();
                mouseDown = true;
                return true;
            }

            return false;
        }

        public boolean clickedColorPreview(double mouseX, double mouseY) {
            return isMouseOverColorPreview(mouseX, mouseY);
        }

        public boolean isMouseOverColorPreview(double mouseX, double mouseY) {
            if((mouseX >= colorPreviewDim.x() && mouseX <= colorPreviewDim.xLimit())
                    && (mouseY >= colorPreviewDim.y() && mouseY <= colorPreviewDim.yLimit())) {

                return true;
            }
            return false;
        }

        public void createOrRemoveColorPicker() {
            colorPickerVisible = !colorPickerVisible;
            if(colorPickerVisible) {
                colorPickerElement = createColorPicker();
                screen.addColorPickerWidget(colorPickerElement);
            } else {
                removeColorPicker();
            }
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if(isAvailable() || !mouseDown)
                return false;

            return true;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            mouseDown = false;

            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return super.isMouseOver(mouseX, mouseY);
        }

        @Override
        public void unfocus() {
            removeColorPicker();

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

        public ColorPickerElement createColorPicker() {
            return new ColorPickerElement(colorController, screen, getDimension(), this);
        }

        public void removeColorPicker() {
            screen.clearColorPickerWidget();
            this.colorPickerVisible = false;
            this.colorPickerElement = null;
        }
    }
}
