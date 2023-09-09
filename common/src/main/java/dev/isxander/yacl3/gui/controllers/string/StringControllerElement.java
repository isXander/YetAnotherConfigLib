package dev.isxander.yacl3.gui.controllers.string;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.gui.utils.UndoRedoHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class StringControllerElement extends ControllerWidget<IStringController<?>> {
    protected final boolean instantApply;

    protected String inputField;
    protected Dimension<Integer> inputFieldBounds;
    protected boolean inputFieldFocused;

    protected int caretPos;
    protected int previousCaretPos;
    protected int selectionLength;
    protected int renderOffset;

    protected UndoRedoHelper undoRedoHelper;

    protected float ticks;
    protected float caretTicks;

    private final Component emptyText;

    public StringControllerElement(IStringController<?> control, YACLScreen screen, Dimension<Integer> dim, boolean instantApply) {
        super(control, screen, dim);
        this.instantApply = instantApply;
        inputField = control.getString();
        inputFieldFocused = false;
        selectionLength = 0;
        emptyText = Component.literal("Click to type...").withStyle(ChatFormatting.GRAY);
        control.option().addListener((opt, val) -> {
            inputField = control.getString();
        });
        setDimension(dim);
    }

    @Override
    protected void drawHoveredControl(GuiGraphics graphics, int mouseX, int mouseY, float delta) {

    }

    @Override
    protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Component valueText = getValueText();
        if (!isHovered()) valueText = Component.literal(GuiUtils.shortenString(valueText.getString(), textRenderer, getMaxUnwrapLength(), "...")).setStyle(valueText.getStyle());

        int textX = getDimension().xLimit() - textRenderer.width(valueText) + renderOffset - getXPadding();
        graphics.enableScissor(inputFieldBounds.x(), inputFieldBounds.y() - 2, inputFieldBounds.xLimit() + 1, inputFieldBounds.yLimit() + 4);
        graphics.drawString(textRenderer, valueText, textX, getTextY(), getValueColor(), true);

        if (isHovered()) {
            ticks += delta;

            String text = getValueText().getString();

            graphics.fill(inputFieldBounds.x(), inputFieldBounds.yLimit(), inputFieldBounds.xLimit(), inputFieldBounds.yLimit() + 1, -1);
            graphics.fill(inputFieldBounds.x() + 1, inputFieldBounds.yLimit() + 1, inputFieldBounds.xLimit() + 1, inputFieldBounds.yLimit() + 2, 0xFF404040);

            if (inputFieldFocused || focused) {
                if (caretPos > text.length())
                    caretPos = text.length();

                int caretX = textX + textRenderer.width(text.substring(0, caretPos));
                if (text.isEmpty())
                    caretX = inputFieldBounds.x() + inputFieldBounds.width() / 2;

                if (selectionLength != 0) {
                    int selectionX = textX + textRenderer.width(text.substring(0, caretPos + selectionLength));
                    graphics.fill(caretX, inputFieldBounds.y() - 2, selectionX, inputFieldBounds.yLimit() - 1, 0x803030FF);
                }

                if(caretPos != previousCaretPos) {
                    previousCaretPos = caretPos;
                    caretTicks = 0;
                }

                if ((caretTicks += delta) % 20 <= 10)
                    graphics.fill(caretX, inputFieldBounds.y() - 2, caretX + 1, inputFieldBounds.yLimit() - 1, -1);
            }
        }
        graphics.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isAvailable() && getDimension().isPointInside((int) mouseX, (int) mouseY)) {
            inputFieldFocused = true;

            if (!inputFieldBounds.isPointInside((int) mouseX, (int) mouseY)) {
                caretPos = getDefaultCaretPos();
            } else {
                // gets the appropriate caret position for where you click
                int textX = (int) mouseX - (inputFieldBounds.xLimit() - textRenderer.width(getValueText()));
                int pos = -1;
                int currentWidth = 0;
                for (char ch : inputField.toCharArray()) {
                    pos++;
                    int charLength = textRenderer.width(String.valueOf(ch));
                    if (currentWidth + charLength / 2 > textX) { // if more than halfway past the characters select in front of that char
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
//            if (undoRedoHelper == null) {
//                undoRedoHelper = new UndoRedoHelper(inputField, caretPos, selectionLength);
//            }

            return true;
        } else {
            inputFieldFocused = false;
        }

        return false;
    }

    protected int getDefaultCaretPos() {
        return inputField.length();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!inputFieldFocused)
            return false;

        switch (keyCode) {
            case InputConstants.KEY_ESCAPE, InputConstants.KEY_RETURN -> {
                unfocus();
                return true;
            }
            case InputConstants.KEY_LEFT -> {
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
                        if (Screen.hasControlDown()) {
                            caretPos = findSpaceIndex(true);
                        } else {
                            if (selectionLength != 0) {
                                caretPos += Math.min(selectionLength, 0);
                            } else caretPos--;
                        }
                    }
                    checkRenderOffset();
                    selectionLength = 0;
                }

                return true;
            }
            case InputConstants.KEY_RIGHT -> {
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
                        if (Screen.hasControlDown()) {
                            caretPos = findSpaceIndex(false);
                        } else {
                            if (selectionLength != 0) {
                                caretPos += Math.max(selectionLength, 0);
                            } else caretPos++;
                        }
                        checkRenderOffset();
                    }
                    selectionLength = 0;
                }

                return true;
            }
            case InputConstants.KEY_BACKSPACE -> {
                doBackspace();
                return true;
            }
            case InputConstants.KEY_DELETE -> {
                doDelete();
                return true;
            }
            case InputConstants.KEY_END -> {
                if (Screen.hasShiftDown()) {
                    selectionLength -= inputField.length() - caretPos;
                } else selectionLength = 0;
                caretPos = inputField.length();
                checkRenderOffset();
                return true;
            }
            case InputConstants.KEY_HOME -> {
                if (Screen.hasShiftDown()) {
                    selectionLength += caretPos;
                    caretPos = 0;
                } else {
                    caretPos = 0;
                    selectionLength = 0;
                }
                checkRenderOffset();
                return true;
            }
//            case InputConstants.KEY_Z -> {
//                if (Screen.hasControlDown()) {
//                    UndoRedoHelper.FieldState updated = Screen.hasShiftDown() ? undoRedoHelper.redo() : undoRedoHelper.undo();
//                    if (updated != null) {
//                        System.out.println("Updated: " + updated);
//                        if (modifyInput(builder -> builder.replace(0, inputField.length(), updated.text()))) {
//                            caretPos = updated.cursorPos();
//                            selectionLength = updated.selectionLength();
//                            checkRenderOffset();
//                        }
//                    }
//                    return true;
//                }
//            }
        }

        if (Screen.isPaste(keyCode)) {
            return doPaste();
        } else if (Screen.isCopy(keyCode)) {
            return doCopy();
        } else if (Screen.isCut(keyCode)) {
            return doCut();
        } else if (Screen.isSelectAll(keyCode)) {
            return doSelectAll();
        }

        return false;
    }

    protected boolean doPaste() {
        this.write(client.keyboardHandler.getClipboard());
        updateUndoHistory();
        return true;
    }

    protected boolean doCopy() {
        if (selectionLength != 0) {
            client.keyboardHandler.setClipboard(getSelection());
            return true;
        }
        return false;
    }

    protected boolean doCut() {
        if (selectionLength != 0) {
            client.keyboardHandler.setClipboard(getSelection());
            this.write("");
            updateUndoHistory();
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
        if (textRenderer.width(inputField) < getUnshiftedLength()) {
            renderOffset = 0;
            return;
        }

        int textX = getDimension().xLimit() - textRenderer.width(inputField) - getXPadding();
        int caretX = textX + textRenderer.width(inputField.substring(0, caretPos));

        int minX = getDimension().xLimit() - getXPadding() - getUnshiftedLength();
        int maxX = minX + getUnshiftedLength();

        if (caretX + renderOffset < minX) {
            renderOffset = minX - caretX;
        } else if (caretX + renderOffset > maxX) {
            renderOffset = maxX - caretX;
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!inputFieldFocused)
            return false;

        if (!Screen.hasControlDown()) {
            write(Character.toString(chr));
            updateUndoHistory();
            return true;
        }

        return false;
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
        updateUndoHistory();
    }

    protected void doDelete() {
        if (selectionLength != 0) {
            write("");
        } else if (caretPos < inputField.length()) {
            modifyInput(builder -> builder.deleteCharAt(caretPos));
        }
        updateUndoHistory();
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

    protected void updateUndoHistory() {
//        undoRedoHelper.save(inputField, caretPos, selectionLength);
    }

    public int getUnshiftedLength() {
        if (optionNameString.isEmpty())
            return getDimension().width() - getXPadding() * 2;
        return getDimension().width() / 8 * 5;
    }

    public int getMaxUnwrapLength() {
        if (optionNameString.isEmpty())
            return getDimension().width() - getXPadding() * 2;
        return getDimension().width() / 2;
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
                fromIndex -= 2;
            i = this.inputField.lastIndexOf(" ", fromIndex) + 1;
        } else {
            if (caretPos < inputField.length())
                fromIndex += 1;
            i = this.inputField.indexOf(" ", fromIndex) + 1;

            if (i == 0) i = inputField.length();
        }

        return i;
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        inputFieldFocused = focused;
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

        int width = Math.max(6, Math.min(textRenderer.width(getValueText()), getUnshiftedLength()));
        inputFieldBounds = Dimension.ofInt(dim.xLimit() - getXPadding() - width, dim.centerY() - textRenderer.lineHeight / 2, width, textRenderer.lineHeight);
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
        return !isHovered() ? Math.min(getHoveredControlWidth(), getMaxUnwrapLength()) : getHoveredControlWidth();
    }

    @Override
    protected int getHoveredControlWidth() {
        return Math.min(textRenderer.width(getValueText()), getUnshiftedLength());
    }

    @Override
    protected Component getValueText() {
        if (!inputFieldFocused && inputField.isEmpty())
            return emptyText;

        return instantApply || !inputFieldFocused ? control.formatValue() : Component.literal(inputField);
    }
}
