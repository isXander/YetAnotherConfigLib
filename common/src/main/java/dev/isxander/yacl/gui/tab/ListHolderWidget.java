package dev.isxander.yacl.gui.tab;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.gui.ElementListWidgetExt;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ListHolderWidget<T extends ElementListWidgetExt<?>> extends AbstractWidget implements ContainerEventHandler {
    private final Supplier<ScreenRectangle> dimensions;
    private final T list;

    public ListHolderWidget(Supplier<ScreenRectangle> dimensions, T list) {
        super(0, 0, 100, 0, CommonComponents.EMPTY);
        this.dimensions = dimensions;
        this.list = list;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float deltaTick) {
        ScreenRectangle dimensions = this.dimensions.get();
        this.setX(dimensions.left());
        this.setY(dimensions.top());
        this.width = dimensions.width();
        this.height = dimensions.height();
        this.list.updateDimensions(dimensions);
        this.list.render(guiGraphics, mouseX, mouseY, deltaTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.list.updateNarration(output);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return ImmutableList.of(this.list);
    }

    public T getList() {
        return list;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.list.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.list.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.list.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.list.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        return this.list.keyPressed(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        return this.list.charTyped(c, i);
    }

    @Override
    public boolean isDragging() {
        return this.list.isDragging();
    }

    @Override
    public void setDragging(boolean dragging) {
        this.list.setDragging(dragging);
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.list.getFocused();
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) {
        this.list.setFocused(listener);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return this.list.nextFocusPath(event);
    }

    @Nullable
    @Override
    public ComponentPath getCurrentFocusPath() {
        return this.list.getCurrentFocusPath();
    }
}
