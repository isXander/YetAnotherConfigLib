package dev.isxander.yacl.test;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.ActionController;
import dev.isxander.yacl.gui.controllers.EnumController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.slider.LongSliderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> YetAnotherConfigLib.createBuilder()
                .title(Text.of("Test GUI"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Control Examples"))
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.of("Tick Box"))
                                .tooltip(Text.of("Super long tooltip that is very descriptive to show off the text wrapping features of the thingy yes whwowwoow"))
                                .binding(
                                        false,
                                        () -> TestSettings.tickbox,
                                        (value) -> TestSettings.tickbox = value
                                )
                                .controller(TickBoxController::new)
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Text.of("Int Slider that is cut off because the slider"))
                                .binding(
                                        0,
                                        () -> TestSettings.intSlider,
                                        (value) -> TestSettings.intSlider = value
                                )
                                .controller(opt -> new IntegerSliderController(opt, 0, 3, 1))
                                .build())
                        .option(Option.createBuilder(double.class)
                                .name(Text.of("Double Slider"))
                                .binding(
                                        0.0,
                                        () -> TestSettings.doubleSlider,
                                        (value) -> TestSettings.doubleSlider = value
                                )
                                .controller(opt -> new DoubleSliderController(opt, 0, 3, 0.05))
                                .build())
                        .option(Option.createBuilder(float.class)
                                .name(Text.of("Float Slider"))
                                .binding(
                                        0f,
                                        () -> TestSettings.floatSlider,
                                        (value) -> TestSettings.floatSlider = value
                                )
                                .controller(opt -> new FloatSliderController(opt, 0, 3, 0.1f))
                                .build())
                        .option(Option.createBuilder(long.class)
                                .name(Text.of("Long Slider"))
                                .binding(
                                        0L,
                                        () -> TestSettings.longSlider,
                                        (value) -> TestSettings.longSlider = value
                                )
                                .controller(opt -> new LongSliderController(opt, 0, 1_000_000, 100))
                                .build())
                        .option(Option.createBuilder(TestSettings.Alphabet.class)
                                .name(Text.of("Enum Cycler"))
                                .binding(
                                        TestSettings.Alphabet.A,
                                        () -> TestSettings.enumOption,
                                        (value) -> TestSettings.enumOption = value
                                )
                                .controller(opt -> new EnumController<>(opt, TestSettings.Alphabet.class))
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Text.of("Button \"Option\""))
                                .action(() -> SystemToast.add(MinecraftClient.getInstance().getToastManager(), SystemToast.Type.TUTORIAL_HINT, Text.of("Button Pressed"), Text.of("Button option was invoked!")))
                                .controller(ActionController::new)
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Group Test"))
                        .option(Option.createBuilder(boolean.class)
                                .name(Text.of("Root Test"))
                                .binding(
                                        false,
                                        () -> TestSettings.groupTestRoot,
                                        value -> TestSettings.groupTestRoot = value
                                )
                                .controller(TickBoxController::new)
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("First Group"))
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.of("First Group Test 1"))
                                        .binding(
                                                false,
                                                () -> TestSettings.groupTestFirstGroup,
                                                value -> TestSettings.groupTestFirstGroup = value
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.of("First Group Test 2"))
                                        .binding(
                                                false,
                                                () -> TestSettings.groupTestFirstGroup2,
                                                value -> TestSettings.groupTestFirstGroup2 = value
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.empty())
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.of("Second Group Test"))
                                        .binding(
                                                false,
                                                () -> TestSettings.groupTestSecondGroup,
                                                value -> TestSettings.groupTestSecondGroup = value
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .build())
                .build().generateScreen(parent);
    }

    private static class TestSettings {
        private static boolean tickbox = false;
        private static int intSlider = 0;
        private static double doubleSlider = 0;
        private static float floatSlider = 0;
        private static long longSlider = 0;
        private static Alphabet enumOption = Alphabet.A;

        private static boolean groupTestRoot = false;
        private static boolean groupTestFirstGroup = false;
        private static boolean groupTestFirstGroup2 = false;
        private static boolean groupTestSecondGroup = false;

        public enum Alphabet {
            A, B, C
        }
    }
}
