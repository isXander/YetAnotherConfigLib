package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

//? if >=1.21.9 {
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}

public class PopupControllerScreen extends Screen {
    private final YACLScreen backgroundYaclScreen;
    private final ControllerPopupWidget<?> controllerPopup;
    public PopupControllerScreen(YACLScreen backgroundYaclScreen, ControllerPopupWidget<?> controllerPopup) {
        super(controllerPopup.popupTitle()); //Gets narrated by the narrator
        this.backgroundYaclScreen = backgroundYaclScreen;
        this.controllerPopup = controllerPopup;
    }


    @Override
    protected void init() {
        this.addRenderableWidget(this.controllerPopup);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        this.backgroundYaclScreen.resize(minecraft, width, height);
        this.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        controllerPopup.renderBackground(graphics, mouseX, mouseY, delta);
        this.backgroundYaclScreen.render(graphics, -1, -1, delta); //mouseX/Y set to -1 to prevent hovering outlines

        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        // in 1.21.6+ renderBackground isn't called in render, it's called earlier before the blur pass
        //? if >=1.21.6
        this.backgroundYaclScreen.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }


    //? if >=1.21.9 {
    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (!super.mouseClicked(mouseButtonEvent, bl)) {
            this.onClose();
            return false;
        }
        return true;
    }
    //?} else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            this.onClose();
            return false;
        }
        return true;
    }
    *///?}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (controllerPopup.mouseScrolled(mouseX, mouseY, horizontal, vertical)) {
            return true;
        }
        backgroundYaclScreen.mouseScrolled(mouseX, mouseY, horizontal, vertical); //mouseX & mouseY are needed here
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        controllerPopup.mouseMoved(mouseX, mouseY);
    }

    //? if >=1.21.9 {
    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        return controllerPopup.charTyped(characterEvent);
    }
    //?} else {
    /*@Override
    public boolean charTyped(char codePoint, int modifiers) {
        return controllerPopup.charTyped(codePoint, modifiers);
    }
    *///?}


    //? if >=1.21.9 {
    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        return controllerPopup.keyPressed(keyEvent);
    }
    //?} else {
    /*@Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return controllerPopup.keyPressed(keyCode, scanCode, modifiers);
    }
    *///?}

    @Override
    public void tick() {
        super.tick();
        this.backgroundYaclScreen.tick();
    }

    @Override
    public void onClose() {
        this.minecraft.screen = backgroundYaclScreen;
        this.controllerPopup.close();
    }

}
