package dev.isxander.yacl.gui.controllers;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Util;

import java.io.File;
import java.util.List;

/**
 * Simply renders some text as a label.
 */
public class LabelController implements Controller<Text> {
    private final Option<Text> option;
    /**
     * Constructs a label controller
     *
     * @param option bound option
     */
    public LabelController(Option<Text> option) {
        this.option = option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<Text> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return option().pendingValue();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new LabelControllerElement(screen, widgetDimension);
    }

    public class LabelControllerElement extends AbstractWidget {
        private List<OrderedText> wrappedText;
        protected MultilineText wrappedTooltip;

        protected final YACLScreen screen;

        public LabelControllerElement(YACLScreen screen, Dimension<Integer> dim) {
            super(dim);
            this.screen = screen;
            option().addListener((opt, pending) -> updateTooltip());
            updateTooltip();
            updateText();
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            updateText();

            float y = dim.y();
            for (OrderedText text : wrappedText) {
                textRenderer.drawWithShadow(matrices, text, dim.x(), y + getYPadding(), option().available() ? -1 : 0xFFA0A0A0);
                y += textRenderer.fontHeight;
            }
        }

        @Override
        public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            if (isMouseOver(mouseX, mouseY)) {
                YACLScreen.renderMultilineTooltip(matrices, textRenderer, wrappedTooltip, dim.centerX(), dim.y() - 5, dim.yLimit() + 5, screen.width, screen.height);

                Style style = getStyle(mouseX, mouseY);
                if (style != null && style.getHoverEvent() != null) {
                    HoverEvent hoverEvent = style.getHoverEvent();
                    HoverEvent.ItemStackContent itemStackContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
                    if (itemStackContent != null) {
                        ItemStack stack = itemStackContent.asStack();
                        screen.renderTooltip(matrices, screen.getTooltipFromItem(stack), stack.getTooltipData(), mouseX, mouseY);
                    } else {
                        HoverEvent.EntityContent entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
                        if (entityContent != null) {
                            if (this.client.options.advancedItemTooltips) {
                                screen.renderTooltip(matrices, entityContent.asTooltip(), mouseX, mouseY);
                            }
                        } else {
                            Text text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
                            if (text != null) {
                                MultilineText multilineText = MultilineText.create(textRenderer, text, dim.width());
                                YACLScreen.renderMultilineTooltip(matrices, textRenderer, multilineText, dim.centerX(), dim.y(), dim.yLimit(), screen.width, screen.height);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY))
                return false;

            Style style = getStyle((int) mouseX, (int) mouseY);
            return screen.handleTextClick(style);
        }

        protected Style getStyle(int mouseX, int mouseY) {
            int x = mouseX - dim.x();
            int y = mouseY - dim.y() - getYPadding();
            int line = y / textRenderer.fontHeight;

            if (x < 0 || x > dim.xLimit()) return null;
            if (y < 0 || y > dim.yLimit()) return null;
            if (line < 0 || line >= wrappedText.size()) return null;

            return textRenderer.getTextHandler().getStyleAt(wrappedText.get(line), x);
        }

        private int getYPadding() {
            return 3;
        }

        private void updateText() {
            wrappedText = textRenderer.wrapLines(formatValue(), dim.width());
            dim.setHeight(wrappedText.size() * textRenderer.fontHeight + getYPadding() * 2);
        }

        private void updateTooltip() {
            this.wrappedTooltip = MultilineText.create(textRenderer, option().tooltip(), screen.width / 3 * 2 - 10);
        }

        @Override
        public boolean matchesSearch(String query) {
            return formatValue().getString().toLowerCase().contains(query.toLowerCase());
        }
    }
}
