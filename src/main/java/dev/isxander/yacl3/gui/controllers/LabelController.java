package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.GuiUtils;
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

            GuiUtils.pushPose(graphics);
            GuiUtils.translateZ(graphics, 100);
            if (isMouseOver(mouseX, mouseY)) {
                Style style = getStyle(mouseX, mouseY);
                if (style != null && style.getHoverEvent() != null) {
                    HoverEvent hoverEvent = style.getHoverEvent();

                    //? if >=1.21.6 {
                    graphics.renderComponentHoverEffect(textRenderer, style, mouseX, mouseY);
                    //?} elif >=1.21.5 {
                    /*if (hoverEvent instanceof HoverEvent.ShowItem showItem) {
                        ItemStack stack = showItem.item();
                        renderItemStackTooltip(graphics, mouseX, mouseY, stack);
                    } else if (hoverEvent instanceof HoverEvent.ShowEntity showEntity) {
                        HoverEvent.EntityTooltipInfo entity = showEntity.entity();
                        renderEntityTooltip(graphics, mouseX, mouseY, entity);
                    } else if (hoverEvent instanceof HoverEvent.ShowText showText) {
                        Component text = showText.value();
                        renderTextTooltip(graphics, mouseX, mouseY, text);
                    }
                    *///?} else {
                    /*@Nullable HoverEvent.ItemStackInfo itemStackContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
                    @Nullable HoverEvent.EntityTooltipInfo entityContent = hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY);
                    @Nullable Component text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);

                    if (itemStackContent != null) {
                        ItemStack stack = itemStackContent.getItemStack();
                        renderItemStackTooltip(graphics, mouseX, mouseY, stack);
                    } else if (entityContent != null) {
                        renderEntityTooltip(graphics, mouseX, mouseY, entityContent);
                    } else if (text != null) {
                        renderTextTooltip(graphics, mouseX, mouseY, text);
                    }
                    *///?}
                }

                //? if >=1.21.9 {
                if (style != null && style.getClickEvent() != null) {
                    graphics.requestCursor(com.mojang.blaze3d.platform.cursor.CursorTypes.POINTING_HAND);
                }
                //?}
            }
            GuiUtils.popPose(graphics);
        }

        //? if <=1.21.5 {
        /*private void renderItemStackTooltip(GuiGraphics graphics, int mouseX, int mouseY, ItemStack itemStack) {
            graphics.renderTooltip(textRenderer, Screen.getTooltipFromItem(client, itemStack), itemStack.getTooltipImage(), mouseX, mouseY);
        }
        private void renderEntityTooltip(GuiGraphics graphics, int mouseX, int mouseY, HoverEvent.EntityTooltipInfo entity) {
            if (this.client.options.advancedItemTooltips) {
                graphics.renderComponentTooltip(textRenderer, entity.getTooltipLines(), mouseX, mouseY);
            }
        }
        private void renderTextTooltip(GuiGraphics graphics, int mouseX, int mouseY, Component text) {
            MultiLineLabel multilineText = MultiLineLabel.create(textRenderer, text, getDimension().width());
            YACLScreen.renderMultilineTooltip(graphics, textRenderer, multilineText, getDimension().centerX(), getDimension().y(), getDimension().yLimit(), screen.width, screen.height);
        }
        *///?}

        @Override
        public boolean onMouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY))
                return false;

            Style style = getStyle((int) mouseX, (int) mouseY);

            if(style == null)
                return false;

            // TODO: reimplement
            //? if >=1.21.11 {
            return false;
            //?} else {
            /*return screen.handleComponentClicked(style);
            *///?}
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
            //? if >=1.21.11 {
            return null;
            //?} else {
            /*return textRenderer.getSplitter().componentStyleAtWidth(wrappedText.get(line), x);
            *///?}
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
