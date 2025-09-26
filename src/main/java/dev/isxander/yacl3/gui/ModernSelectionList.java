//? if >=1.21.9 {
package dev.isxander.yacl3.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

public abstract class ModernSelectionList<E extends ModernSelectionList.Entry<E>> extends ContainerObjectSelectionList<E> {
    private Double prevScrollAmount = null;

    public ModernSelectionList(Minecraft minecraft, int width, int height, int y, int defaultEntryHeight) {
        super(minecraft, width, height, y, defaultEntryHeight);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.prevScrollAmount == null || this.prevScrollAmount != this.scrollAmount()) {
            this.prevScrollAmount = this.scrollAmount();
            System.out.println(this.scrollAmount());
        }

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void repositionEntries() {
        // triggers super.repositionEntries() without the need for a mixin accessor
        this.setScrollAmount(this.scrollAmount());
    }

    protected boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), false);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        return this.mouseClicked(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
    }

    protected boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)));
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        return this.mouseReleased(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
    }

    protected boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        return super.mouseDragged(new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0)), dx, dy);

    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dx, double dy) {
        return this.mouseDragged(mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button(), dx, dy);
    }

    protected boolean keyPressed(int key, int scancode, int modifiers) {
        return super.keyPressed(new KeyEvent(key, scancode, modifiers));
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        return this.keyPressed(keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers());
    }

    protected boolean charTyped(char ch, int modifiers) {
        return super.charTyped(new CharacterEvent(ch, modifiers));
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        return this.charTyped((char) characterEvent.codepoint(), characterEvent.modifiers());
    }

    public static abstract class Entry<E extends ModernSelectionList.Entry<E>> extends ContainerObjectSelectionList.Entry<E> {
        protected boolean keyPressed(int key, int scancode, int modifiers) {
            return super.keyPressed(new KeyEvent(key, scancode, modifiers));
        }

        @Override
        public boolean keyPressed(KeyEvent keyEvent) {
            return this.keyPressed(keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers());
        }

        protected boolean charTyped(char ch, int modifiers) {
            return super.charTyped(new CharacterEvent(ch, modifiers));
        }

        @Override
        public boolean charTyped(CharacterEvent characterEvent) {
            return this.charTyped((char) characterEvent.codepoint(), characterEvent.modifiers());
        }
    }
}
//?}
