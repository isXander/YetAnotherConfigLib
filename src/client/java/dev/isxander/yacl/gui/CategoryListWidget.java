package dev.isxander.yacl.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.gui.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class CategoryListWidget extends ElementListWidgetExt<CategoryListWidget.CategoryEntry> {
    private final YACLScreen yaclScreen;

    public CategoryListWidget(MinecraftClient client, YACLScreen yaclScreen, int screenWidth, int screenHeight) {
        super(client, 0, 0, screenWidth / 3, yaclScreen.searchFieldWidget.getY() - 5, true);
        this.yaclScreen = yaclScreen;
        setRenderBackground(false);
        setRenderHorizontalShadows(false);

        for (ConfigCategory category : yaclScreen.config.categories()) {
            addEntry(new CategoryEntry(category));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderUtils.enableScissor(0, 0, width, height);
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
    protected int getScrollbarPositionX() {
        return width - 2;
    }

    @Override
    protected void renderBackground(MatrixStack matrices) {

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
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (mouseY > bottom) {
                mouseY = -20;
            }

            categoryButton.setY(y);
            categoryButton.render(matrices, mouseX, mouseY, tickDelta);
        }

        public void postRender(MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
            categoryButton.renderHoveredTooltip(matrices);
        }

        @Override
        public int getItemHeight() {
            return 21;
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(categoryButton);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(categoryButton);
        }
    }
}
