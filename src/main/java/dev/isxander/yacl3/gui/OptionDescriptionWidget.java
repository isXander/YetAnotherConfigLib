package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class OptionDescriptionWidget extends AbstractWidget {
    private static final int AUTO_SCROLL_TIMER = 1500;
    private static final float AUTO_SCROLL_SPEED = 1; // lines per second

    private @Nullable DescriptionWithName description;
    private List<FormattedCharSequence> wrappedText;

    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final Font font = minecraft.font;

    private Supplier<ScreenRectangle> dimensions;

    private float targetScrollAmount, currentScrollAmount;
    private int maxScrollAmount;
    private int descriptionY;

    private int lastInteractionTime;
    private boolean scrollingBackward;

    public OptionDescriptionWidget(Supplier<ScreenRectangle> dimensions, @Nullable DescriptionWithName description) {
        super(0, 0, 0, 0, description == null ? Component.empty() : description.name());
        this.dimensions = dimensions;
        this.setOptionDescription(description);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (description == null) return;

        currentScrollAmount = Mth.lerp(delta * 0.5f, currentScrollAmount, targetScrollAmount);

        ScreenRectangle dimensions = this.dimensions.get();
        this.setX(dimensions.left());
        this.setY(dimensions.top());
        this.width = dimensions.width();
        this.height = dimensions.height();

        int y = getY();

        int nameWidth = font.width(description.name());
        if (nameWidth > getWidth()) {
            renderScrollingString(graphics, font, description.name(), getX(), y, getX() + getWidth(), y + font.lineHeight, -1);
        } else {
            graphics.drawString(font, description.name(), getX(), y, 0xFFFFFF);
        }

        y += 5 + font.lineHeight;

        graphics.enableScissor(getX(), y, getX() + getWidth(), getY() + getHeight());

        y -= (int)currentScrollAmount;

        if (description.description().image().isDone()) {
            var image = description.description().image().join();
            if (image.isPresent()) {
                y += image.get().render(graphics, getX(), y, getWidth(), delta) + 5;
            }
        }

        if (wrappedText == null)
            wrappedText = font.split(description.description().text(), getWidth());

        descriptionY = y;
        for (var line : wrappedText) {
            graphics.drawString(font, line, getX(), y, 0xFFFFFF);
            y += font.lineHeight;
        }

        graphics.disableScissor();

        maxScrollAmount = Math.max(0, y + (int)currentScrollAmount - getY() - getHeight());

        if (isHoveredOrFocused()) {
            lastInteractionTime = currentTimeMS();
        }
        Style hoveredStyle = getDescStyle(mouseX, mouseY);
        if (hoveredStyle != null && hoveredStyle.getHoverEvent() != null) {
            graphics.renderComponentHoverEffect(font, hoveredStyle, mouseX, mouseY);
        }

        if (isFocused()) {
            graphics.renderOutline(getX(), getY(), getWidth(), getHeight(), -1);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Style clickedStyle = getDescStyle((int) mouseX, (int) mouseY);
        if (clickedStyle != null && clickedStyle.getClickEvent() != null) {
            if (minecraft.screen.handleComponentClicked(clickedStyle)) {
                playDownSound(minecraft.getSoundManager());
                return true;
            }
            return false;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, /*? if >1.20.2 {*/ double horizontal, /*?}*/ double vertical) {
        if (isMouseOver(mouseX, mouseY)) {
            targetScrollAmount = Mth.clamp(targetScrollAmount - (int) vertical * 10, 0, maxScrollAmount);
            lastInteractionTime = currentTimeMS();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isFocused()) {
            switch (keyCode) {
                case InputConstants.KEY_UP ->
                    targetScrollAmount = Mth.clamp(targetScrollAmount - 10, 0, maxScrollAmount);
                case InputConstants.KEY_DOWN ->
                    targetScrollAmount = Mth.clamp(targetScrollAmount + 10, 0, maxScrollAmount);
                default -> {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void tick() {
        if (description != null) {
            description.description().image()
                    .getNow(Optional.empty())
                    .ifPresent(ImageRenderer::tick);
        }

        float pxPerTick = AUTO_SCROLL_SPEED / 20f * font.lineHeight;
        if (maxScrollAmount > 0 && currentTimeMS() - lastInteractionTime > AUTO_SCROLL_TIMER) {
            if (scrollingBackward) {
                pxPerTick *= -1;
                if (targetScrollAmount + pxPerTick < 0) {
                    scrollingBackward = false;
                    lastInteractionTime = currentTimeMS();
                }
            } else {
                if (targetScrollAmount + pxPerTick > maxScrollAmount) {
                    scrollingBackward = true;
                    lastInteractionTime = currentTimeMS();
                }
            }

            targetScrollAmount = Mth.clamp(targetScrollAmount + pxPerTick, 0, maxScrollAmount);
        }
    }

    private Style getDescStyle(int mouseX, int mouseY) {
        if (!clicked(mouseX, mouseY))
            return null;

        int x = mouseX - getX();
        int y = mouseY - descriptionY;

        if (x < 0 || x > getX() + getWidth()) return null;
        if (y < 0 || y > getY() + getHeight()) return null;

        int line = y / font.lineHeight;

        if (line >= wrappedText.size()) return null;

        return font.getSplitter().componentStyleAtWidth(wrappedText.get(line), x);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
        if (description != null) {
            builder.add(NarratedElementType.TITLE, description.name());
            builder.add(NarratedElementType.HINT, description.description().text());
        }

    }

    public void setOptionDescription(DescriptionWithName description) {
        this.description = description;
        this.wrappedText = null;
        this.targetScrollAmount = 0;
        this.currentScrollAmount = 0;
        this.lastInteractionTime = currentTimeMS();
    }

    private int currentTimeMS() {
        return (int)(Blaze3D.getTime() * 1000);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        // prevents focusing on this widget
        return null;
    }

}
