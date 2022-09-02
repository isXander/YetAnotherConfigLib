package dev.isxander.yacl.gui.controllers;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

/**
 * This controller renders a tickbox
 */
public class TickBoxController implements Controller<Boolean> {
    private final Option<Boolean> option;

    /**
     * Constructs a tickbox controller
     *
     * @param option bound option
     */
    public TickBoxController(Option<Boolean> option) {
        this.option = option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<Boolean> option() {
        return option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Text formatValue() {
        return Text.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerWidget<TickBoxController> provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new TickBoxControllerElement(this, screen, widgetDimension);
    }

    @ApiStatus.Internal
    public static class TickBoxControllerElement extends ControllerWidget<TickBoxController> {
        private TickBoxControllerElement(TickBoxController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        protected void drawHoveredControl(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            int outlineSize = 10;
            int outlineX1 = dim.xLimit() - getXPadding() - outlineSize;
            int outlineY1 = dim.centerY() - outlineSize / 2;
            int outlineX2 = dim.xLimit() - getXPadding();
            int outlineY2 = dim.centerY() + outlineSize / 2;
            drawOutline(matrices, outlineX1 + 1, outlineY1 + 1, outlineX2 + 1, outlineY2 + 1, 1, 0xFF404040);
            drawOutline(matrices, outlineX1, outlineY1, outlineX2, outlineY2, 1, -1);
            if (control.option().pendingValue()) {
                DrawableHelper.fill(matrices, outlineX1 + 3, outlineY1 + 3, outlineX2 - 1, outlineY2 - 1, 0xFF404040);
                DrawableHelper.fill(matrices, outlineX1 + 2, outlineY1 + 2, outlineX2 - 2, outlineY2 - 2, -1);
            }
        }

        @Override
        protected void drawValueText(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            if (!hovered)
                drawHoveredControl(matrices, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY))
                return false;

            toggleSetting();
            return true;
        }

        @Override
        protected int getHoveredControlWidth() {
            return 10;
        }

        public void toggleSetting() {
            control.option().requestSet(!control.option().pendingValue());
            playDownSound();
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_SPACE && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
                return false;
            }

            toggleSetting();

            return true;
        }
    }
}
