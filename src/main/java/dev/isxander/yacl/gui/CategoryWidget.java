package dev.isxander.yacl.gui;

import dev.isxander.yacl.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

import java.util.List;

public class CategoryWidget extends ButtonWidget {
    public float hoveredTicks = 0;
    public final List<OrderedText> wrappedDescription;
    private int prevMouseX, prevMouseY;

    public CategoryWidget(YACLScreen screen, ConfigCategory category, int x, int y, int width, int height) {
        super(x, y, width, height, category.name(), btn -> screen.changeCategory(screen.categoryButtons.indexOf(btn)));
        wrappedDescription = MinecraftClient.getInstance().textRenderer.wrapLines(category.tooltip(), screen.width / 2);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        if (isHovered() && prevMouseX == mouseX && prevMouseY == mouseY) {
            hoveredTicks += delta;
        } else {
            hoveredTicks = 0;
        }

        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }
}
