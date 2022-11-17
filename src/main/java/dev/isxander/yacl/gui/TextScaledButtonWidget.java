package dev.isxander.yacl.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class TextScaledButtonWidget extends ButtonWidget {
    public float textScale;

    public TextScaledButtonWidget(int x, int y, int width, int height, float textScale, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.textScale = textScale;
    }

    public TextScaledButtonWidget(int x, int y, int width, int height, float textScale, Text message, PressAction onPress, Tooltip tooltip) {
        this(x, y, width, height, textScale, message, onPress);
        setTooltip(tooltip);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // prevents super from rendering text
        Text message = getMessage();
        setMessage(Text.empty());

        super.renderButton(matrices, mouseX, mouseY, delta);

        setMessage(message);
        int j = this.active ? 16777215 : 10526880;
        OrderedText orderedText = getMessage().asOrderedText();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        matrices.push();
        matrices.translate(((this.getX() + this.width / 2f) - textRenderer.getWidth(orderedText) * textScale / 2), (float)this.getY() + (this.height - 8 * textScale) / 2f / textScale, 0);
        matrices.scale(textScale, textScale, 1);
        textRenderer.drawWithShadow(matrices, orderedText, 0, 0, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        matrices.pop();
    }
}
