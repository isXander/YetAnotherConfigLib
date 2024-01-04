package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class PopupColorPickerScreen extends Screen {
    private final YACLScreen backgroundYaclScreen;
    private final OptionListWidget optionListWidget;
    private final ColorPickerElement colorPicker;
    public double initialScrollAmount;
    public double scrollAmount;
    public double smoothScrollAmount;
    public int controllerY;
    public int maxScroll;
    public PopupColorPickerScreen(YACLScreen backgroundYaclScreen, OptionListWidget optionListWidget, ColorPickerElement colorPicker) {
        super(Component.literal("Color Picker")); //translatable string?
        this.backgroundYaclScreen = backgroundYaclScreen;
        this.optionListWidget = optionListWidget;
        this.colorPicker = colorPicker;
        this.scrollAmount = optionListWidget.getScrollAmount();
        this.controllerY = optionListWidget.getActiveColorPickerY();
        this.maxScroll = optionListWidget.getMaxScroll();
        this.initialScrollAmount = scrollAmount;
        smoothScrollAmount = scrollAmount;
    }


    @Override
    protected void init() {
        //FIXME - Resizing window breaks backgroundYaclScreen, perhaps init() the yaclScreen?
        this.addRenderableWidget(this.colorPicker);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        smoothScrollAmount = Mth.lerp(Minecraft.getInstance().getDeltaFrameTime() * 0.5, smoothScrollAmount, scrollAmount);
        double colorPickerY = colorPicker.getDimension().height() - smoothScrollAmount + initialScrollAmount + controllerY - 20;
        colorPicker.setDimension(colorPicker.getDimension().withY((int) colorPickerY));
        if(colorPicker.getEntryWidget().isMouseOverColorPreview(mouseX, mouseY)) {
            colorPicker.getEntryWidget().hoveredOverColorPreview = true;
        } else {
            colorPicker.getEntryWidget().hoveredOverColorPreview = false;
        }
        this.backgroundYaclScreen.render(guiGraphics, -1, -1, partialTick); //mouseX/Y set to -1 to prevent hovering outlines
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        backgroundYaclScreen.mouseScrolled(mouseX, mouseY, scrollX, scrollY); //mouseX & mouseY are needed here
        if(mouseY > optionListWidget.getY() + 1) { //prevents color picker scrolling while scrolling through category list
            int scrollBarPos = backgroundYaclScreen.tabArea.width() / 3 * 2 + 1;
            if(mouseX < scrollBarPos) { //prevent color picker scrolling while scrolling through a controller description
                scrollAmount = Mth.clamp(scrollAmount - (scrollY + scrollX) * 20, 0, maxScroll);
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onClose() {
        this.minecraft.screen = backgroundYaclScreen;
        colorPicker.getEntryWidget().removeColorPicker();
    }

}
