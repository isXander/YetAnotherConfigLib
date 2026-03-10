package dev.isxander.yacl3.gui.tab;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.gui.render.ColorGradientRenderState;
import dev.isxander.yacl3.mixin.TabNavigationBarAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
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
        for (TabButton tabButton : accessor.yacl$getTabButtons()) {
            if (tabButton.tab() instanceof TabExt tab) {
                tabButton.setTooltip(tab.getTooltip());
            }
        }
    }

    @Override
    public void arrangeElements() {
        ImmutableList<TabButton> tabButtons = accessor.yacl$getTabButtons();
        int noScrollWidth = accessor.yacl$getWidth() - NAVBAR_MARGIN*2;

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

        Layout layout = ((TabNavigationBarAccessor) this).yacl$getLayout();
        layout.arrangeElements();
        layout.setY(0);
        scrollOffset = 0;

        layout.setX(Math.max((accessor.yacl$getWidth() - allTabsWidth) / 2, NAVBAR_MARGIN));
        this.maxScrollOffset = Math.max(0, allTabsWidth - noScrollWidth);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.pose().pushMatrix();
        // render option list BELOW the navbar without need to scissor

        super.extractRenderState(graphics, mouseX, mouseY, delta);

        LinearLayout layout = accessor.yacl$getLayout();
        // draw right fade
        if (this.scrollOffset < this.maxScrollOffset - NAVBAR_MARGIN) {
            int right = accessor.yacl$getWidth();
            ColorGradientRenderState.createHorizontal(
                    graphics,
                    right - 40,
                    layout.getY(),
                    right,
                    layout.getY() + layout.getHeight(),
                    0x00000000, 0xFF000000
            ).submit(graphics);

            graphics.text(font, "→", right - 10, layout.getY() + (layout.getHeight() - font.lineHeight) / 2, 0xFFFFFFFF, false);
        }

        // draw left fade
        if (this.scrollOffset > NAVBAR_MARGIN) {
            ColorGradientRenderState.createHorizontal(
                    graphics,
                    0,
                    layout.getY(),
                    40,
                    layout.getY() + layout.getHeight(),
                    0xFF000000, 0x00000000
            ).submit(graphics);

            graphics.text(font, "←", 5, layout.getY() + (layout.getHeight() - font.lineHeight) / 2, 0xFFFFFFFF, false);
        }

        graphics.pose().popMatrix();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        this.setScrollOffset(this.scrollOffset - (int) (vertical * 15) - (int) (horizontal * 15));
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY <= 24;
    }

    public void setScrollOffset(int scrollOffset) {
        Layout layout = ((TabNavigationBarAccessor) this).yacl$getLayout();

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
        } else if (tabButton.getX() + tabButton.getWidth() > accessor.yacl$getWidth() - NAVBAR_MARGIN) {
            this.setScrollOffset(this.scrollOffset + (tabButton.getX() + tabButton.getWidth() - (accessor.yacl$getWidth() - NAVBAR_MARGIN)));
        }
    }

    public ImmutableList<Tab> getTabs() {
        return accessor.yacl$getTabs();
    }

    public TabManager getTabManager() {
        return accessor.yacl$getTabManager();
    }
}
