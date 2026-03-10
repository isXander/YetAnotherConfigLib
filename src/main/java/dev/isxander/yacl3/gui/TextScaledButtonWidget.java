package dev.isxander.yacl3.gui;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextScaledButtonWidget extends TooltipButtonWidget {
    public float textScale;

    public TextScaledButtonWidget(Screen screen, int x, int y, int width, int height, float textScale, Component message, Component tooltip, OnPress onPress) {
        super(screen, x, y, width, height, message, tooltip, onPress);
        this.textScale = textScale;
    }

    public TextScaledButtonWidget(Screen screen, int x, int y, int width, int height, float textScale, Component message, OnPress onPress) {
        this(screen, x, y, width, height, textScale, message, null, onPress);
    }

    // FIXME: i cannot figure out how to compensate a scale with a translation so for now the reset button is small fuck you
    @Override
    protected void extractDefaultLabel(ActiveTextCollector output) {
        super.extractDefaultLabel(output);
    }
}
