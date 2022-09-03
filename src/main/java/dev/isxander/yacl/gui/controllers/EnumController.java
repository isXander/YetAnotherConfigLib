package dev.isxander.yacl.gui.controllers;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.NameableEnum;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.YACLScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.util.function.Function;

/**
 * Simple controller type that displays the enum on the right.
 * <p>
 * Cycles forward with left click, cycles backward with right click or when shift is held
 *
 * @param <T> enum type
 */
public class EnumController<T extends Enum<T>> implements Controller<T> {
    private final Option<T> option;
    private final Function<T, Text> valueFormatter;
    private final Class<T> enumClass;

    /**
     * Constructs a cycling enum controller with a default value formatter.
     * The default value formatter first searches if the
     * enum is a {@link NameableEnum} else, just use {@link Enum#name()}
     *
     * @param option bound option
     * @param enumClass class of enum
     */
    public EnumController(Option<T> option, Class<T> enumClass) {
        this(option, enumClass, value -> {
            if (value instanceof NameableEnum nameableEnum)
                return nameableEnum.getDisplayName();
            return Text.of(value.name());
        });
    }

    /**
     * Constructs a cycling enum controller.
     *
     * @param option bound option
     * @param enumClass class of enum
     * @param valueFormatter format the enum into any {@link Text}
     */
    public EnumController(Option<T> option, Class<T> enumClass, Function<T, Text> valueFormatter) {
        this.option = option;
        this.valueFormatter = valueFormatter;
        this.enumClass = enumClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Option<T> option() {
        return option;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Text formatValue() {
        return valueFormatter.apply(option().pendingValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ControllerWidget<EnumController<T>> provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new EnumControllerElement<>(this, screen, widgetDimension, enumClass.getEnumConstants());
    }

    @ApiStatus.Internal
    public static class EnumControllerElement<T extends Enum<T>> extends ControllerWidget<EnumController<T>> {
        private final T[] values;

        public EnumControllerElement(EnumController<T> control, YACLScreen screen, Dimension<Integer> dim, T[] values) {
            super(control, screen, dim);
            this.values = values;
        }

        public void cycleValue(int increment) {
            int targetIdx = control.option().pendingValue().ordinal() + increment;
            if (targetIdx >= values.length) {
                targetIdx -= values.length;
            } else if (targetIdx < 0) {
                targetIdx += values.length;
            }
            control.option().requestSet(values[targetIdx]);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY) || (button != 0 && button != 1))
                return false;

            playDownSound();
            cycleValue(button == 1 || Screen.hasShiftDown() || Screen.hasControlDown() ? -1 : 1);

            return true;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!focused && !hovered)
                return false;

            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_DOWN ->
                        cycleValue(-1);
                case GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_UP ->
                        cycleValue(1);
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_KP_ENTER ->
                        cycleValue(Screen.hasControlDown() || Screen.hasShiftDown() ? -1 : 1);
                default -> {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }
    }
}
