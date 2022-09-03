package dev.isxander.yacl.gui;

import dev.isxander.yacl.api.ConfigCategory;
import net.minecraft.client.sound.SoundManager;

public class CategoryWidget extends TooltipButtonWidget {
    private final int categoryIndex;

    public CategoryWidget(YACLScreen screen, ConfigCategory category, int categoryIndex, int x, int y, int width, int height) {
        super(screen, x, y, width, height, category.name(), category.tooltip(), btn -> screen.changeCategory(screen.categoryButtons.indexOf(btn)));
        this.categoryIndex = categoryIndex;
    }

    private boolean isCurrentCategory() {
        return ((YACLScreen) screen).currentCategoryIdx == categoryIndex;
    }

    @Override
    protected int getYImage(boolean hovered) {
        return super.getYImage(hovered || isCurrentCategory());
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        if (!isCurrentCategory())
            super.playDownSound(soundManager);
    }
}
