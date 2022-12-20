package dev.isxander.yacl.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SearchFieldWidget extends TextFieldWidget {
    private Text emptyText;
    private final YACLScreen yaclScreen;
    private final TextRenderer textRenderer;

    private boolean isEmpty = true;

    public SearchFieldWidget(YACLScreen yaclScreen, TextRenderer textRenderer, int x, int y, int width, int height, Text text, Text emptyText) {
        super(textRenderer, x, y, width, height, text);
        setChangedListener(string -> update());
        setTextPredicate(string -> !string.endsWith(" ") && !string.startsWith(" "));
        this.yaclScreen = yaclScreen;
        this.textRenderer = textRenderer;
        this.emptyText = emptyText;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        if (isVisible() && isEmpty()) {
            textRenderer.drawWithShadow(matrices, emptyText, getX() + 4, this.getY() + (this.height - 8) / 2f, 0x707070);
        }
    }

    private void update() {
        boolean wasEmpty = isEmpty;
        isEmpty = getText().isEmpty();

        if (isEmpty && wasEmpty)
            return;

        if (!isEmpty && yaclScreen.getCurrentCategoryIdx() != -1)
            yaclScreen.changeCategory(-1);
        if (isEmpty && yaclScreen.getCurrentCategoryIdx() == -1)
            yaclScreen.changeCategory(0);

        yaclScreen.optionList.expandAllGroups();
        yaclScreen.optionList.recacheViewableChildren();

        yaclScreen.optionList.setScrollAmount(0);
        yaclScreen.categoryList.setScrollAmount(0);
    }

    public String getQuery() {
        return getText().toLowerCase();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public Text getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(Text emptyText) {
        this.emptyText = emptyText;
    }
}
