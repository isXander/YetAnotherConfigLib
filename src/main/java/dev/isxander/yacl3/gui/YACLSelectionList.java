package dev.isxander.yacl3.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import org.jspecify.annotations.NonNull;

public abstract class YACLSelectionList<E extends YACLSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
    private boolean doneRefresh;

    public YACLSelectionList(Minecraft minecraft, int width, int height, int y) {
        super(minecraft, width, height, y, 20);
    }

    @Override
    public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // idk why but the scroll is broken initially and this fixes it.
        if (!doneRefresh) {
            this.repositionEntries();
            this.doneRefresh = true;
        }

        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
    }

    public static <T extends YACLSelectionList<?>> WidgetAndType<T> asWidget(T list) {
        return WidgetAndType.ofWidget(list);
    }

    public static abstract class Entry<E extends YACLSelectionList.Entry<E>> extends ContainerObjectSelectionList.Entry<E> {
        public Entry(YACLSelectionList<E> parent) {
            super();
        }
    }
}
