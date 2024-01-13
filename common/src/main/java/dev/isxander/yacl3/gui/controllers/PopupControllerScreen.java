package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

public class PopupControllerScreen extends Screen {
    private final YACLScreen backgroundYaclScreen;
    private final OptionListWidget optionListWidget;
    private final ControllerPopupWidget controllerPopup;
    public double initialScrollAmount;
    public double scrollAmount;
    public double smoothScrollAmount;
    public int controllerY;
    public int maxScroll;
    public PopupControllerScreen(YACLScreen backgroundYaclScreen, OptionListWidget optionListWidget, ControllerPopupWidget controllerPopup) {
        super(controllerPopup.popupTitle()); //Gets narrated by the narrator - switch to translatable string?
        this.backgroundYaclScreen = backgroundYaclScreen;
        this.optionListWidget = optionListWidget;
        this.controllerPopup = controllerPopup;
        this.scrollAmount = optionListWidget.getScrollAmount();
        this.controllerY = optionListWidget.getActivePopupControllerY();
        this.maxScroll = optionListWidget.getMaxScroll();
        this.initialScrollAmount = scrollAmount;
        smoothScrollAmount = scrollAmount;
    }


    @Override
    protected void init() {
        this.addRenderableWidget(this.controllerPopup);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        minecraft.setScreen(backgroundYaclScreen);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        smoothScrollAmount = Mth.lerp(Minecraft.getInstance().getDeltaFrameTime() * 0.5, smoothScrollAmount, scrollAmount);
        double colorPickerY = controllerPopup.getDimension().height() - smoothScrollAmount + initialScrollAmount + controllerY - 20;
        controllerPopup.setDimension(controllerPopup.getDimension().withY((int) colorPickerY));
        controllerPopup.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
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
    public boolean charTyped(char codePoint, int modifiers) {
        return controllerPopup.charTyped(codePoint, modifiers);
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return controllerPopup.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        this.minecraft.screen = backgroundYaclScreen;
        controllerPopup.close();
    }

}
