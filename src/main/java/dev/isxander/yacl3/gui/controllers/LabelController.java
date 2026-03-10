package dev.isxander.yacl3.gui.controllers;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Simply renders some text as a label.
 */
public record LabelController(Option<Component> option) implements Controller<Component> {
    /**
     * Constructs a label controller
     *
     * @param option bound option
     */
    public LabelController {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<Component> option() {
        return option;
    }

    @Override
    public Component formatValue() {
        return option().pendingValue();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new LabelControllerElement(screen, widgetDimension);
    }

    public class LabelControllerElement extends AbstractWidget {
        private List<FormattedCharSequence> wrappedText;
        protected MultiLineLabel wrappedTooltip;
        protected boolean focused;

        protected final YACLScreen screen;

        public LabelControllerElement(YACLScreen screen, Dimension<Integer> dim) {
            super(dim);
            this.screen = screen;
            option().addListener((opt, pending) -> updateTooltip());
            updateTooltip();
            updateText();
        }

        @Override
        public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
            updateText();

            int y = getDimension().y();
            ActiveTextCollector textCollector = graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR);
            Style textStyle = Style.EMPTY.withColor(option().available() ? -1 : 0xFFA0A0A0);
            for (FormattedCharSequence text : wrappedText) {
                textCollector.accept(
                        getDimension().x() + getXPadding(),
                        y + getYPadding(),
                        GuiUtils.overrideStyle(text, textStyle)
                );
                y += textRenderer.lineHeight;
            }

            if (isFocused()) {
                graphics.fill(getDimension().x() - 1, getDimension().y() - 1, getDimension().xLimit() + 1, getDimension().y(), -1);
                graphics.fill(getDimension().x() - 1, getDimension().y() - 1, getDimension().x(), getDimension().yLimit() + 1, -1);
                graphics.fill(getDimension().x() - 1, getDimension().yLimit(), getDimension().xLimit() + 1, getDimension().yLimit() + 1, -1);
                graphics.fill(getDimension().xLimit(), getDimension().y() - 1, getDimension().xLimit() + 1, getDimension().yLimit() + 1, -1);
            }

            graphics.pose().pushMatrix();
            if (isMouseOver(mouseX, mouseY)) {
                Style style = getStyle(mouseX, mouseY);

                if (style != null && style.getClickEvent() != null) {
                    graphics.requestCursor(CursorTypes.POINTING_HAND);
                }
            }
            graphics.pose().popMatrix();
        }

        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
            if (!isMouseOver(event.x(), event.y()))
                return false;

            Style style = getStyle((int) event.x(), (int) event.y());

            if (style == null)
                return false;

            // TODO: reimplement
            return false;
        }

        @Nullable
        protected Style getStyle(int mouseX, int mouseY) {
            if (!getDimension().isPointInside(mouseX, mouseY))
                return null;

            int x = mouseX - getDimension().x() - getXPadding();
            int y = mouseY - getDimension().y() - getYPadding();
            int line = y / textRenderer.lineHeight;

            if (x < 0 || x > getDimension().xLimit()) return null;
            if (y < 0 || y > getDimension().yLimit()) return null;
            if (line < 0 || line >= wrappedText.size()) return null;

            // TODO reimplement
            return null;
        }

        private int getXPadding() {
            return 4;
        }

        private int getYPadding() {
            return 3;
        }

        private void updateText() {
            wrappedText = textRenderer.split(formatValue(), getDimension().width() - getXPadding() * 2);
            setDimension(getDimension().withHeight(wrappedText.size() * textRenderer.lineHeight + getYPadding() * 2));
        }

        private void updateTooltip() {
            this.wrappedTooltip = MultiLineLabel.create(textRenderer, option().tooltip(), screen.width / 3 * 2 - 10);
        }

        @Override
        public boolean matchesSearch(String query) {
            return formatValue().getString().toLowerCase().contains(query.toLowerCase());
        }

        @Nullable
        @Override
        public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
            if (!option().available())
                return null;
            return !this.isFocused() ? ComponentPath.leaf(this) : null;
        }

        @Override
        public boolean isFocused() {
            return focused;
        }

        @Override
        public void setFocused(boolean focused) {
            this.focused = focused;
        }

        @Override
        public void updateNarration(NarrationElementOutput builder) {
            builder.add(NarratedElementType.TITLE, formatValue());
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.FOCUSED;
        }
    }
}
