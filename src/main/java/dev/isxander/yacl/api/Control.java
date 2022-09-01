package dev.isxander.yacl.api;

import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.controllers.ControlWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public interface Control<T> {
    Option<T> option();

    Text formatValue();

    ControlWidget<?> provideWidget(Screen screen, Dimension<Integer> widgetDimension);
}
