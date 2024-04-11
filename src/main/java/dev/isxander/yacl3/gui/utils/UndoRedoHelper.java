package dev.isxander.yacl3.gui.utils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UndoRedoHelper {
    private final List<FieldState> history = new ArrayList<>();
    private int index = 0;

    public UndoRedoHelper(String text, int cursorPos, int selectionLength) {
        history.add(new FieldState(text, cursorPos, selectionLength));
    }

    public void save(String text, int cursorPos, int selectionLength) {
        int max = history.size();
        history.subList(index, max).clear();
        history.add(new FieldState(text, cursorPos, selectionLength));
        index++;
    }

    public @Nullable FieldState undo() {
        index--;
        index = Math.max(index, 0);

        if (history.isEmpty())
            return null;
        return history.get(index);
    }

    public @Nullable FieldState redo() {
        if (index < history.size() - 1) {
            index++;
            return history.get(index);
        } else {
            return null;
        }
    }

    public record FieldState(String text, int cursorPos, int selectionLength) {}
}
