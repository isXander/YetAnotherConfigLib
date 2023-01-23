package dev.isxander.yacl.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.gui.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.List;

public class CategoryListWidget extends ElementListWidgetExt<CategoryListWidget.CategoryEntry> {
    private final YACLScreen yaclScreen;

    public CategoryListWidget(Minecraft client, YACLScreen yaclScreen, int screenWidth, int screenHeight) {
        super(client, 0, 0, screenWidth / 3, yaclScreen.searchFieldWidget.y - 5, true);
        this.yaclScreen = yaclScreen;
        setRenderBackground(false);
        setRenderTopAndBottom(false);

        for (ConfigCategory category : yaclScreen.config.categories()) {
            addEntry(new CategoryEntry(category));
        }
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        GuiUtils.enableScissor(0, 0, width, height);
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableScissor();
    }

    @Override
    public int getRowWidth() {
        return Math.min(width - width / 10, 396);
    }

    @Override
    public int getRowLeft() {
        return super.getRowLeft() - 2;
    }

    @Override
    protected int getScrollbarPosition() {
        return width - 2;
    }

    @Override
    protected void renderBackground(PoseStack matrices) {

    }

    public class CategoryEntry extends Entry<CategoryEntry> {
        private final CategoryWidget categoryButton;
        public final int categoryIndex;

        public CategoryEntry(ConfigCategory category) {
            this.categoryIndex = yaclScreen.config.categories().indexOf(category);
            categoryButton = new CategoryWidget(
                    yaclScreen,
                    category,
                    categoryIndex,
                    getRowLeft(), 0,
                    getRowWidth(), 20
            );
        }

        @Override
        public void render(PoseStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (mouseY > y1) {
                mouseY = -20;
            }

            categoryButton.y = y;
            categoryButton.render(matrices, mouseX, mouseY, tickDelta);
        }

        public void postRender(PoseStack matrices, int mouseX, int mouseY, float tickDelta) {
            categoryButton.renderHoveredTooltip(matrices);
        }

        @Override
        public int getItemHeight() {
            return 21;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(categoryButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(categoryButton);
        }
    }
}
