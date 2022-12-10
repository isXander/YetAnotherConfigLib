package dev.isxander.yacl.gui.controllers.string;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
import dev.isxander.yacl.gui.utils.RenderUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class StringControllerElement extends ControllerWidget<IStringController<?>> {
    protected final boolean instantApply;

    protected String inputField;
    protected Dimension<Integer> inputFieldBounds;
    protected boolean inputFieldFocused;

    protected int caretPos;
    protected int selectionLength;

    protected int renderOffset;

    protected float ticks;

    private final Text emptyText;

    public StringControllerElement(IStringController<?> control, YACLScreen screen, Dimension<Integer> dim, boolean instantApply) {
        super(control, screen, dim);
        this.instantApply = instantApply;
        inputField = control.getString();
        inputFieldFocused = false;
        selectionLength = 0;
        emptyText = Text.literal("Click to type...").formatted(Formatting.GRAY);
        control.option().addListener((opt, val) -> inputField = control.getString());
        setDimension(dim);
    }

    @Override
    protected void drawHoveredControl(MatrixStack matrices, int mouseX, int mouseY, float delta) {

    }

    @Override
    protected void drawValueText(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Text valueText = getValueText();
        if (!isHovered()) valueText = Text.literal(RenderUtils.shortenString(valueText.getString(), textRenderer, getDimension().width() / 2, "...")).setStyle(valueText.getStyle());

        int overflowWidth = textRenderer.getWidth(inputField.substring(inputField.length() - renderOffset));

        matrices.push();
        int textX = getDimension().xLimit() - textRenderer.getWidth(valueText) + overflowWidth - getXPadding();
        matrices.translate(textX, getTextY(), 0);
        RenderUtils.enableScissor(inputFieldBounds.x(), inputFieldBounds.y(), inputFieldBounds.width() + 1, inputFieldBounds.height() + 2);
        textRenderer.drawWithShadow(matrices, valueText, 0, 0, getValueColor());
        matrices.pop();

        if (isHovered()) {
            ticks += delta;

            String text = getValueText().getString();

            DrawableHelper.fill(matrices, inputFieldBounds.x(), inputFieldBounds.yLimit(), inputFieldBounds.xLimit(), inputFieldBounds.yLimit() + 1, -1);
            DrawableHelper.fill(matrices, inputFieldBounds.x() + 1, inputFieldBounds.yLimit() + 1, inputFieldBounds.xLimit() + 1, inputFieldBounds.yLimit() + 2, 0xFF404040);

            if (inputFieldFocused || focused) {
                int caretX = textX + textRenderer.getWidth(text.substring(0, caretPos)) - 1;
                if (text.isEmpty())
                    caretX = inputFieldBounds.x() + inputFieldBounds.width() / 2;

                if (ticks % 20 <= 10) {
                    DrawableHelper.fill(matrices, caretX, inputFieldBounds.y(), caretX + 1, inputFieldBounds.yLimit(), -1);
                }

                if (selectionLength != 0) {
                    int selectionX = textX + textRenderer.getWidth(text.substring(0, caretPos + selectionLength));
                    DrawableHelper.fill(matrices, caretX, inputFieldBounds.y() - 1, selectionX, inputFieldBounds.yLimit(), 0x803030FF);
                }
            }
        }
        RenderSystem.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isAvailable() && getDimension().isPointInside((int) mouseX, (int) mouseY)) {
            inputFieldFocused = true;

            if (!inputFieldBounds.isPointInside((int) mouseX, (int) mouseY)) {
                caretPos = getDefaultCarotPos();
            } else {
                // gets the appropriate caret position for where you click
                int textX = (int) mouseX - (inputFieldBounds.xLimit() - textRenderer.getWidth(getValueText()));
                int pos = -1;
                int currentWidth = 0;
                for (char ch : inputField.toCharArray()) {
                    pos++;
                    int charLength = textRenderer.getWidth(String.valueOf(ch));
                    if (currentWidth + charLength / 2 > textX) { // if more than half way past the characters select in front of that char
                        caretPos = pos;
                        break;
                    } else if (pos == inputField.length() - 1) {
                        // if we have reached the end and no matches, it must be the second half of the char so the last position
                        caretPos = pos + 1;
                    }
                    currentWidth += charLength;
                }

                selectionLength = 0;
            }
            return true;
        } else {
            inputFieldFocused = false;
        }

        return false;
    }

    protected int getDefaultCarotPos() {
        return inputField.length();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!inputFieldFocused)
            return false;

        switch (keyCode) {
            case InputUtil.GLFW_KEY_ESCAPE, InputUtil.GLFW_KEY_ENTER -> {
                unfocus();
                return true;
            }
            case InputUtil.GLFW_KEY_LEFT -> {
                if (Screen.hasShiftDown()) {
                    if (Screen.hasControlDown()) {
                        int spaceChar = findSpaceIndex(true);
                        selectionLength += caretPos - spaceChar;
                        caretPos = spaceChar;
                    } else if (caretPos > 0) {
                        caretPos--;
                        selectionLength += 1;
                    }
                    checkRenderOffset();
                } else {
                    if (caretPos > 0) {
                        if (selectionLength != 0)
                            caretPos += Math.min(selectionLength, 0);
                        else
                            caretPos--;
                    }
                    checkRenderOffset();
                    selectionLength = 0;
                }

                return true;
            }
            case InputUtil.GLFW_KEY_RIGHT -> {
                if (Screen.hasShiftDown()) {
                    if (Screen.hasControlDown()) {
                        int spaceChar = findSpaceIndex(false);
                        selectionLength -= spaceChar - caretPos;
                        caretPos = spaceChar;
                    } else if (caretPos < inputField.length()) {
                        caretPos++;
                        selectionLength -= 1;
                    }
                    checkRenderOffset();
                } else {
                    if (caretPos < inputField.length()) {
                        if (selectionLength != 0)
                            caretPos += Math.max(selectionLength, 0);
                        else
                            caretPos++;
                        checkRenderOffset();
                    }
                    selectionLength = 0;
                }

                return true;
            }
            case InputUtil.GLFW_KEY_BACKSPACE -> {
                doBackspace();
                return true;
            }
            case InputUtil.GLFW_KEY_DELETE -> {
                doDelete();
                return true;
            }
        }

        if (Screen.isPaste(keyCode)) {
            return doPaste();
        } else if (Screen.isCopy(keyCode)) {
            return  doCopy();
        } else if (Screen.isCut(keyCode)) {
            return doCut();
        } else if (Screen.isSelectAll(keyCode)) {
            return doSelectAll();
        }

        return false;
    }

    protected boolean doPaste() {
        this.write(client.keyboard.getClipboard());
        return true;
    }

    protected boolean doCopy() {
        if (selectionLength != 0) {
            client.keyboard.setClipboard(getSelection());
            return true;
        }
        return false;
    }

    protected boolean doCut() {
        if (selectionLength != 0) {
            client.keyboard.setClipboard(getSelection());
            this.write("");
            return true;
        }
        return false;
    }

    protected boolean doSelectAll() {
        caretPos = inputField.length();
        checkRenderOffset();
        selectionLength = -caretPos;
        return true;
    }

    protected void checkRenderOffset() {
        if (textRenderer.getWidth(inputField) < getUnshiftedLength()) {
            renderOffset = 0;
            return;
        }

        String inp = inputField.substring(0, inputField.length() - renderOffset);
        int obstructionWidth = textRenderer.getWidth(inp) - getUnshiftedLength();
        int length = inp.length() - textRenderer.trimToWidth(inp, obstructionWidth).length();

        if (caretPos < inputField.length() - renderOffset - length)
            renderOffset = inputField.length() - caretPos - length;
        if (caretPos > inputField.length() - renderOffset)
            renderOffset = inputField.length() - caretPos;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!inputFieldFocused)
            return false;

        write(Character.toString(chr));

        return true;
    }

    protected void doBackspace() {
        if (selectionLength != 0) {
            write("");
        } else if (caretPos > 0) {
            if (modifyInput(builder -> builder.deleteCharAt(caretPos - 1))) {
                caretPos--;
                checkRenderOffset();
            }
        }
    }

    protected void doDelete() {
        if (selectionLength != 0) {
            write("");
        } else if (caretPos < inputField.length()) {
            modifyInput(builder -> builder.deleteCharAt(caretPos));
        }
    }

    public void write(String string) {
        if (selectionLength == 0) {
            if (modifyInput(builder -> builder.insert(caretPos, string))) {
                caretPos += string.length();
                checkRenderOffset();
            }
        } else {
            int start = getSelectionStart();
            int end = getSelectionEnd();

            if (modifyInput(builder -> builder.replace(start, end, string))) {
                caretPos = start + string.length();
                selectionLength = 0;
                checkRenderOffset();
            }
        }
    }

    public boolean modifyInput(Consumer<StringBuilder> consumer) {
        StringBuilder temp = new StringBuilder(inputField);
        consumer.accept(temp);
        if (!control.isInputValid(temp.toString()))
            return false;
        inputField = temp.toString();
        if (instantApply)
            updateControl();
        return true;
    }

    public int getUnshiftedLength() {
        return getDimension().width() / 8 * 5;
    }

    public int getSelectionStart() {
        return Math.min(caretPos, caretPos + selectionLength);
    }

    public int getSelectionEnd() {
        return Math.max(caretPos, caretPos + selectionLength);
    }

    protected String getSelection() {
        return inputField.substring(getSelectionStart(), getSelectionEnd());
    }

    protected int findSpaceIndex(boolean reverse) {
        int i;
        int fromIndex = caretPos;
        if (reverse) {
            if (caretPos > 0)
                fromIndex -= 1;
            i = this.inputField.lastIndexOf(" ", fromIndex);

            if (i == -1) i = 0;
        } else {
            if (caretPos < inputField.length())
                fromIndex += 1;
            i = this.inputField.indexOf(" ", fromIndex);

            if (i == -1) i = inputField.length();
        }

        return i;
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return inputFieldFocused = super.changeFocus(lookForwards);
    }

    @Override
    public void unfocus() {
        super.unfocus();
        inputFieldFocused = false;
        renderOffset = 0;
        if (!instantApply) updateControl();
    }

    @Override
    public void setDimension(Dimension<Integer> dim) {
        super.setDimension(dim);

        int width = Math.max(6, Math.min(textRenderer.getWidth(getValueText()), getUnshiftedLength()));
        inputFieldBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - width, dim.centerY() - textRenderer.fontHeight / 2, width, textRenderer.fontHeight);
    }

    @Override
    public boolean isHovered() {
        return super.isHovered() || inputFieldFocused;
    }

    protected void updateControl() {
        control.setFromString(inputField);
    }

    @Override
    protected int getUnhoveredControlWidth() {
        return !isHovered() ? Math.min(getHoveredControlWidth(), getDimension().width() / 2) : getHoveredControlWidth();
    }

    @Override
    protected int getHoveredControlWidth() {
        return Math.min(textRenderer.getWidth(getValueText()), getUnshiftedLength());
    }

    @Override
    protected Text getValueText() {
        if (!inputFieldFocused && inputField.isEmpty())
            return emptyText;

        return instantApply || !inputFieldFocused ? control.formatValue() : Text.of(inputField);
    }
}
