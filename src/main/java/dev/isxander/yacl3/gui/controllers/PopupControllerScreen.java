package dev.isxander.yacl3.gui.controllers;

import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.InputType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

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
    protected void repositionElements() {
        super.repositionElements();
        this.onClose();
    }

    @Override
    public void extractRenderState(
            @NonNull GuiGraphicsExtractor graphics
            //? if <26.4
            //,int mouseX, int mouseY, float a
    ) {
        //? if >=26.4 {
        controllerPopup.extractBackground(graphics, graphics.mouseX(), graphics.mouseY(), graphics.partialTicks());

        // set input type to keyboard to prevent hovering outlines
        var prevInputType = minecraft.getLastInputType();
        minecraft.setLastInputType(InputType.KEYBOARD_ARROW);
        this.backgroundYaclScreen.extractRenderState(graphics);
        minecraft.setLastInputType(prevInputType);

        super.extractRenderState(graphics);
        //?} else {
        /*controllerPopup.extractBackground(graphics, mouseX, mouseY, a);

        // mouseX/Y set to -1 to prevent hovering outline
        this.backgroundYaclScreen.extractRenderState(graphics, -1, -1, a);

        super.extractRenderState(graphics, mouseX, mouseY, a);
        *///?}
    }

    @Override
    public void extractBackground(
            GuiGraphicsExtractor guiGraphics
            //? if <26.4
            //,int mouseX, int mouseY, float partialTick
    ) {
        this.backgroundYaclScreen.extractBackground(
                guiGraphics
                //? if <26.4
                //,mouseX, mouseY, partialTick
        );
    }


    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (!super.mouseClicked(event, doubleClick)) {
            this.onClose();
            return false;
        }
        return true;
    }

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

    @Override
    public boolean charTyped(@NonNull CharacterEvent characterEvent) {
        return controllerPopup.charTyped(characterEvent);
    }

    @Override
    public boolean keyPressed(@NonNull KeyEvent keyEvent) {
        return controllerPopup.keyPressed(keyEvent);
    }

    @Override
    public void tick() {
        super.tick();
        this.backgroundYaclScreen.tick();
    }

    @Override
    public void onClose() {
        GuiUtils.setScreen(backgroundYaclScreen, true);
        this.controllerPopup.close();
    }

}
