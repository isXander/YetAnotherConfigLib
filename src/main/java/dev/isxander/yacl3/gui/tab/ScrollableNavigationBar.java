package dev.isxander.yacl3.gui.tab;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.mixin.TabNavigationBarAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class ScrollableNavigationBar extends TabNavigationBar {
    private static final int NAVBAR_MARGIN = 28;

    private static final Font font = Minecraft.getInstance().font;

    private int scrollOffset;
    private int maxScrollOffset;

    private final TabNavigationBarAccessor accessor;

    public ScrollableNavigationBar(int width, TabManager tabManager, Iterable<? extends Tab> tabs) {
        super(width, tabManager, ImmutableList.copyOf(tabs));
        this.accessor = (TabNavigationBarAccessor) this;

        // add tab tooltips to the tab buttons
        for (TabButton tabButton : accessor.getTabButtons()) {
            if (tabButton.tab() instanceof TabExt tab) {
                tabButton.setTooltip(tab.getTooltip());
            }
        }
    }

    @Override
    public void arrangeElements() {
        ImmutableList<TabButton> tabButtons = accessor.getTabButtons();
        int noScrollWidth = accessor.getWidth() - NAVBAR_MARGIN*2;

        int allTabsWidth = 0;
        // first pass: set the width of each tab button
        for (TabButton tabButton : tabButtons) {
            int buttonWidth = font.width(tabButton.getMessage()) + 20;
            allTabsWidth += buttonWidth;
            tabButton.setWidth(buttonWidth);
        }

        if (allTabsWidth < noScrollWidth) {
            int equalWidth = noScrollWidth / tabButtons.size();
            var smallTabs = tabButtons.stream().filter(btn -> btn.getWidth() < equalWidth).toList();
            var bigTabs = tabButtons.stream().filter(btn -> btn.getWidth() >= equalWidth).toList();
            int leftoverWidth = noScrollWidth - bigTabs.stream().mapToInt(AbstractWidget::getWidth).sum();
            int equalWidthForSmallTabs = leftoverWidth / smallTabs.size();
            for (TabButton tabButton : smallTabs) {
                tabButton.setWidth(equalWidthForSmallTabs);
            }

            allTabsWidth = noScrollWidth;
        }

        Layout layout = ((TabNavigationBarAccessor) this).getLayout();
        layout.arrangeElements();
        layout.setY(0);
        scrollOffset = 0;

        layout.setX(Math.max((accessor.getWidth() - allTabsWidth) / 2, NAVBAR_MARGIN));
        this.maxScrollOffset = Math.max(0, allTabsWidth - noScrollWidth);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        graphics.pose().pushPose();
        // render option list BELOW the navbar without need to scissor
        graphics.pose().translate(0, 0, 10);

        super.render(graphics, mouseX, mouseY, delta);

        graphics.pose().popPose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, /*? if >1.20.2 {*/ double horizontal, /*?}*/ double vertical) {
        this.setScrollOffset(this.scrollOffset - (int)(vertical*15));
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY <= 24;
    }

    public void setScrollOffset(int scrollOffset) {
        Layout layout = ((TabNavigationBarAccessor) this).getLayout();

        layout.setX(layout.getX() + this.scrollOffset);
        this.scrollOffset = Mth.clamp(scrollOffset, 0, maxScrollOffset);
        layout.setX(layout.getX() - this.scrollOffset);
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener child) {
        super.setFocused(child);
        if (child instanceof TabButton tabButton) {
            this.ensureVisible(tabButton);
        }
    }

    protected void ensureVisible(TabButton tabButton) {
        if (tabButton.getX() < NAVBAR_MARGIN) {
            this.setScrollOffset(this.scrollOffset - (NAVBAR_MARGIN - tabButton.getX()));
        } else if (tabButton.getX() + tabButton.getWidth() > accessor.getWidth() - NAVBAR_MARGIN) {
            this.setScrollOffset(this.scrollOffset + (tabButton.getX() + tabButton.getWidth() - (accessor.getWidth() - NAVBAR_MARGIN)));
        }
    }

    public ImmutableList<Tab> getTabs() {
        return accessor.getTabs();
    }
}
