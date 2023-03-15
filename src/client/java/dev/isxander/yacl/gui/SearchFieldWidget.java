package dev.isxander.yacl.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class SearchFieldWidget extends EditBox {
    private Component emptyText;
    private final YACLScreen yaclScreen;
    private final Font font;

    private boolean isEmpty = true;

    public SearchFieldWidget(YACLScreen yaclScreen, Font font, int x, int y, int width, int height, Component text, Component emptyText) {
        super(font, x, y, width, height, text);
        setResponder(string -> update());
        setFilter(string -> !string.endsWith("  ") && !string.startsWith(" "));
        this.yaclScreen = yaclScreen;
        this.font = font;
        this.emptyText = emptyText;
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        if (isVisible() && isEmpty()) {
            font.drawShadow(matrices, emptyText, getX() + 4, this.getY() + (this.height - 8) / 2f, 0x707070);
        }
    }

    private void update() {
        boolean wasEmpty = isEmpty;
        isEmpty = getValue().isEmpty();

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
        return getValue().toLowerCase();
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public Component getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(Component emptyText) {
        this.emptyText = emptyText;
    }
}
