package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.MapOptionEntry;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Simply renders some text as a label.
 */
public class LabelController implements Controller<Component> {
    private final Option<Component> option;
    /**
     * Constructs a label controller
     *
     * @param option bound option
     */
    public LabelController(Option<Component> option) {
        this.option = option;
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
        // TODO: How should this be handled?
        if (option() instanceof MapOptionEntry<?, ?> mapOptionEntry) {
            return (Component) ((Map.Entry<?, ?>) mapOptionEntry.pendingValue()).getKey();
        }

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
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            updateText();

            int y = getDimension().y();
            for (FormattedCharSequence text : wrappedText) {
                graphics.drawString(textRenderer, text, getDimension().x() + getXPadding(), y + getYPadding(), option().available() ? -1 : 0xFFA0A0A0, true);
                y += textRenderer.lineHeight;
            }

            if (isFocused()) {
                graphics.fill(getDimension().x() - 1, getDimension().y() - 1, getDimension().xLimit() + 1, getDimension().y(), -1);
                graphics.fill(getDimension().x() - 1, getDimension().y() - 1, getDimension().x(), getDimension().yLimit() + 1, -1);
                graphics.fill(getDimension().x() - 1, getDimension().yLimit(), getDimension().xLimit() + 1, getDimension().yLimit() + 1, -1);
                graphics.fill(getDimension().xLimit(), getDimension().y() - 1, getDimension().xLimit() + 1, getDimension().yLimit() + 1, -1);
            }

            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 100);
            if (isMouseOver(mouseX, mouseY)) {
                YACLScreen.renderMultilineTooltip(graphics, textRenderer, wrappedTooltip, getDimension().centerX(), getDimension().y() - 5, getDimension().yLimit() + 5, screen.width, screen.height);

                Style style = getStyle(mouseX, mouseY);
                if (style != null && style.getHoverEvent() != null) {
                    HoverEvent hoverEvent = style.getHoverEvent();
                    HoverEvent.ItemStackInfo itemStackContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
                    if (itemStackContent != null) {
                        ItemStack stack = itemStackContent.getItemStack();
                        graphics.renderTooltip(textRenderer, Screen.getTooltipFromItem(client, stack), stack.getTooltipImage(), mouseX, mouseY);
                    } else {
                        HoverEvent.EntityTooltipInfo entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
                        if (entityContent != null) {
                            if (this.client.options.advancedItemTooltips) {
                                graphics.renderComponentTooltip(textRenderer, entityContent.getTooltipLines(), mouseX, mouseY);
                            }
                        } else {
                            Component text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
                            if (text != null) {
                                MultiLineLabel multilineText = MultiLineLabel.create(textRenderer, text, getDimension().width());
                                YACLScreen.renderMultilineTooltip(graphics, textRenderer, multilineText, getDimension().centerX(), getDimension().y(), getDimension().yLimit(), screen.width, screen.height);
                            }
                        }
                    }
                }
            }
            graphics.pose().popPose();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY))
                return false;

            Style style = getStyle((int) mouseX, (int) mouseY);
            return screen.handleComponentClicked(style);
        }

        protected Style getStyle(int mouseX, int mouseY) {
            if (!getDimension().isPointInside(mouseX, mouseY))
                return null;

            int x = mouseX - getDimension().x();
            int y = mouseY - getDimension().y() - getYPadding();
            int line = y / textRenderer.lineHeight;

            if (x < 0 || x > getDimension().xLimit()) return null;
            if (y < 0 || y > getDimension().yLimit()) return null;
            if (line < 0 || line >= wrappedText.size()) return null;

            return textRenderer.getSplitter().componentStyleAtWidth(wrappedText.get(line), x);
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
