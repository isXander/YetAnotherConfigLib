package dev.isxander.yacl.gui;

import dev.isxander.yacl.api.ConfigCategory;
import net.minecraft.client.sounds.SoundManager;

public class CategoryWidget extends TooltipButtonWidget {
    private final int categoryIndex;

    public CategoryWidget(YACLScreen screen, ConfigCategory category, int categoryIndex, int x, int y, int width, int height) {
        super(screen, x, y, width, height, category.name(), category.tooltip(), btn -> {
            screen.searchFieldWidget.setValue("");
            screen.changeCategory(categoryIndex);
        });
        this.categoryIndex = categoryIndex;
    }

    private boolean isCurrentCategory() {
        return ((YACLScreen) screen).getCurrentCategoryIdx() == categoryIndex;
    }

    @Override
    protected int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused() || isCurrentCategory()) {
            i = 2;
        }

        return 46 + i * 20;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        if (!isCurrentCategory())
            super.playDownSound(soundManager);
    }
}
