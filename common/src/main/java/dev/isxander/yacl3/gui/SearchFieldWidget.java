package dev.isxander.yacl3.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SearchFieldWidget extends EditBox {
    private Component emptyText;
    private final YACLScreen yaclScreen;
    private final Font font;
    private final Consumer<String> updateConsumer;

    private boolean isEmpty = true;

    public SearchFieldWidget(YACLScreen yaclScreen, Font font, int x, int y, int width, int height, Component text, Component emptyText, Consumer<String> updateConsumer) {
        super(font, x, y, width, height, text);
        setResponder(this::update);
        setFilter(string -> !string.endsWith("  ") && !string.startsWith(" "));
        this.yaclScreen = yaclScreen;
        this.font = font;
        this.emptyText = emptyText;
        this.updateConsumer = updateConsumer;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(graphics, mouseX, mouseY, delta);
        if (isVisible() && isEmpty()) {
            graphics.drawString(font, emptyText, getX() + 4, this.getY() + (this.height - 8) / 2, 0x707070, true);
        }
    }

    private void update(String query) {
        boolean wasEmpty = isEmpty;
        isEmpty = query.isEmpty();

        if (isEmpty && wasEmpty)
            return;

        updateConsumer.accept(query);
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
