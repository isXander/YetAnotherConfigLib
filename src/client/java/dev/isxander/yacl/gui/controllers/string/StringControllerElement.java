package dev.isxander.yacl.gui.controllers.string;

import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
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
        ticks += delta;

        String text = instantApply ? control.getString() : inputField;

        DrawableHelper.fill(matrices, inputFieldBounds.x(), inputFieldBounds.yLimit(), inputFieldBounds.xLimit(), inputFieldBounds.yLimit() + 1, -1);
        DrawableHelper.fill(matrices, inputFieldBounds.x() + 1, inputFieldBounds.yLimit() + 1, inputFieldBounds.xLimit() + 1, inputFieldBounds.yLimit() + 2, 0xFF404040);

        if (inputFieldFocused || focused) {
            int caretX = inputFieldBounds.x() + textRenderer.getWidth(text.substring(0, caretPos)) - 1;
            if (text.isEmpty())
                caretX += inputFieldBounds.width() / 2;

            if (ticks % 20 <= 10) {
                DrawableHelper.fill(matrices, caretX, inputFieldBounds.y(), caretX + 1, inputFieldBounds.yLimit(), -1);
            }

            if (selectionLength != 0) {
                int selectionX = inputFieldBounds.x() + textRenderer.getWidth(text.substring(0, caretPos + selectionLength));
                DrawableHelper.fill(matrices, caretX, inputFieldBounds.y() - 1, selectionX, inputFieldBounds.yLimit(), 0x803030FF);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isAvailable() && inputFieldBounds.isPointInside((int) mouseX, (int) mouseY)) {
            if (!inputFieldFocused) {
                inputFieldFocused = true;
                caretPos = getDefaultCarotPos();
            } else {
                int textWidth = (int) mouseX - inputFieldBounds.x();
                caretPos = textRenderer.trimToWidth(control.getString(), textWidth).length();
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
                } else {
                    if (caretPos > 0) {
                        if (selectionLength != 0)
                            caretPos += Math.min(selectionLength, 0);
                        else
                            caretPos--;
                    }
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
                } else {
                    if (caretPos < inputField.length()) {
                        if (selectionLength != 0)
                            caretPos += Math.max(selectionLength, 0);
                        else
                            caretPos++;
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

        if (canUseShortcuts()) {
            if (Screen.isPaste(keyCode)) {
                this.write(client.keyboard.getClipboard());
                return true;
            } else if (Screen.isCopy(keyCode) && selectionLength != 0) {
                client.keyboard.setClipboard(getSelection());
                return true;
            } else if (Screen.isCut(keyCode) && selectionLength != 0) {
                client.keyboard.setClipboard(getSelection());
                this.write("");
                return true;
            } else if (Screen.isSelectAll(keyCode)) {
                caretPos = inputField.length();
                selectionLength = -caretPos;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!inputFieldFocused)
            return false;

        write(Character.toString(chr));

        return true;
    }

    protected boolean canUseShortcuts() {
        return true;
    }

    protected void doBackspace() {
        if (selectionLength != 0) {
            write("");
        } else if (caretPos > 0) {
            if (modifyInput(builder -> builder.deleteCharAt(caretPos - 1)))
                caretPos--;
        }
    }

    protected void doDelete() {
        if (caretPos < inputField.length()) {
            modifyInput(builder -> builder.deleteCharAt(caretPos));
        }
    }

    public void write(String string) {
        if (selectionLength == 0) {
            String trimmed = textRenderer.trimToWidth(string, getMaxLength() - textRenderer.getWidth(inputField));

            if (modifyInput(builder -> builder.insert(caretPos, trimmed))) {
                caretPos += trimmed.length();
            }
        } else {
            int start = getSelectionStart();
            int end = getSelectionEnd();

            String trimmed = textRenderer.trimToWidth(string, getMaxLength() - textRenderer.getWidth(inputField) + textRenderer.getWidth(inputField.substring(start, end)));

            if (modifyInput(builder -> builder.replace(start, end, trimmed))) {
                caretPos = start + trimmed.length();
                selectionLength = 0;
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

    public int getMaxLength() {
        return getDimension().width() / 8 * 7;
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
        if (!instantApply) updateControl();
    }

    @Override
    public void setDimension(Dimension<Integer> dim) {
        super.setDimension(dim);

        int width = Math.max(6, textRenderer.getWidth(getValueText()));
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
    protected int getHoveredControlWidth() {
        return getUnhoveredControlWidth();
    }

    @Override
    protected Text getValueText() {
        if (!inputFieldFocused && inputField.isEmpty())
            return emptyText;

        return instantApply || !inputFieldFocused ? control.formatValue() : Text.of(inputField);
    }
}
