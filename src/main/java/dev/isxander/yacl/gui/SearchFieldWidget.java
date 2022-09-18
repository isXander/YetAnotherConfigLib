package dev.isxander.yacl.gui;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.OptionGroup;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SearchFieldWidget extends TextFieldWidget {
    private Text emptyText;
    private final YACLScreen yaclScreen;
    private final TextRenderer textRenderer;

    public SearchFieldWidget(YACLScreen yaclScreen, TextRenderer textRenderer, int x, int y, int width, int height, Text text, Text emptyText) {
        super(textRenderer, x, y, width, height, text);
        this.yaclScreen = yaclScreen;
        this.textRenderer = textRenderer;
        this.emptyText = emptyText;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        if (isVisible() && getText().isEmpty()) {
            textRenderer.drawWithShadow(matrices, emptyText, x + 4, this.y + (this.height - 8) / 2f, 0x707070);
        }
    }

    @Override
    public void write(String text) {
        yaclScreen.optionList.setScrollAmount(0);
        yaclScreen.categoryList.setScrollAmount(0);
        for (OptionListWidget.Entry entry : yaclScreen.optionList.children()) {
            if (entry instanceof OptionListWidget.GroupSeparatorEntry groupSeparatorEntry) {
                groupSeparatorEntry.setExpanded(true);
            }
        }

        super.write(text);
    }

    @Override
    public void eraseCharacters(int characterOffset) {
        yaclScreen.optionList.setScrollAmount(0);

        super.eraseCharacters(characterOffset);
    }

    public boolean matches(OptionListWidget.OptionEntry optionEntry, boolean ignoreCategory) {
        return (matchesCategory(optionEntry.category) && !ignoreCategory) || matchesGroup(optionEntry.group) || matchesWidget(optionEntry.widget);
    }

    public boolean matchesCategory(ConfigCategory category) {
        return category.name().getString().toLowerCase().contains(getText().trim());
    }

    public boolean matchesGroup(OptionGroup group) {
        if (group.isRoot())
            return false;

        return group.name().getString().toLowerCase().contains(getText().trim());
    }

    public boolean matchesWidget(AbstractWidget widget) {
        return widget.matchesSearch(getText().trim());
    }

    public Text getEmptyText() {
        return emptyText;
    }

    public void setEmptyText(Text emptyText) {
        this.emptyText = emptyText;
    }
}