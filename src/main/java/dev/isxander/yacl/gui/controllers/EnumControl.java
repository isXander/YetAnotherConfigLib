package dev.isxander.yacl.gui.controllers;

import dev.isxander.yacl.api.Control;
import dev.isxander.yacl.api.NameableEnum;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Function;

public class EnumControl<T extends Enum<T>> implements Control<T> {
    private final Option<T> option;
    private final Function<T, Text> valueFormatter;
    private final Class<T> enumClass;

    public EnumControl(Option<T> option, Class<T> enumClass) {
        this(option, enumClass, value -> {
            if (value instanceof NameableEnum nameableEnum)
                return nameableEnum.getDisplayName();
            return Text.of(value.name());
        });
    }

    public EnumControl(Option<T> option, Class<T> enumClass, Function<T, Text> valueFormatter) {
        this.option = option;
        this.valueFormatter = valueFormatter;
        this.enumClass = enumClass;
    }

    @Override
    public Option<T> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return valueFormatter.apply(option().pendingValue());
    }

    @Override
    public ControlWidget<EnumControl<T>> provideWidget(Screen screen, Dimension<Integer> widgetDimension) {
        return new EnumControlElement<>(this, screen, widgetDimension, enumClass.getEnumConstants());
    }

    public static class EnumControlElement<T extends Enum<T>> extends ControlWidget<EnumControl<T>> {
        private final T[] values;

        public EnumControlElement(EnumControl<T> control, Screen screen, Dimension<Integer> dim, T[] values) {
            super(control, screen, dim);
            this.values = values;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY) || (button != 0 && button != 1))
                return false;

            playDownSound();

            int change = button == 1 || Screen.hasShiftDown() ? -1 : 1;
            int targetIdx = control.option().pendingValue().ordinal() + change;
            if (targetIdx >= values.length) {
                targetIdx -= values.length;
            } else if (targetIdx < 0) {
                targetIdx += values.length;
            }
            control.option().requestSet(values[targetIdx]);
            return true;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }
    }
}
