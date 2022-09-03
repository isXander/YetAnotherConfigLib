package dev.isxander.yacl.gui;

import dev.isxander.yacl.impl.YACLConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class TooltipButtonWidget extends ButtonWidget {
    private float hoveredTicks = 0;
    private int prevMouseX, prevMouseY;

    private final Screen screen;
    private List<OrderedText> wrappedDescription;

    public TooltipButtonWidget(Screen screen, int x, int y, int width, int height, Text message, Text tooltip, PressAction onPress) {
        super(x, y, width, height, message, onPress);
        this.screen = screen;
        setTooltip(tooltip);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        if (isHovered() && (!YACLConstants.HOVER_MOUSE_RESET || (prevMouseX == mouseX && prevMouseY == mouseY))) {
            hoveredTicks += delta;
        } else {
            hoveredTicks = 0;
        }

        prevMouseX = mouseX;
        prevMouseY = mouseY;
    }

    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if (hoveredTicks >= YACLConstants.HOVER_TICKS) {
            screen.renderOrderedTooltip(matrices, wrappedDescription, mouseX, mouseY);
        }
    }

    public void setTooltip(Text tooltip) {
        wrappedDescription = MinecraftClient.getInstance().textRenderer.wrapLines(tooltip, screen.width / 2);
    }
}
