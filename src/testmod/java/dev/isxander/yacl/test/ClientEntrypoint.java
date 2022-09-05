package dev.isxander.yacl.test;

import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.*;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import dev.isxander.yacl.serialization.impl.GsonYACLSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.awt.*;

public class ClientEntrypoint implements ClientModInitializer {
    private static ClientEntrypoint instance;
    private YetAnotherConfigLib yacl;
    
    @Override
    public void onInitializeClient() {
        instance = this;
        makeYacl();
        yacl.serializer().load();
    }

    public static ClientEntrypoint getInstance() {
        return instance;
    }

    public YetAnotherConfigLib getYACL() {
        return yacl;
    }
    
    private void makeYacl() {
        yacl = YetAnotherConfigLib.createBuilder()
                .title(Text.of("Test GUI"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Control Examples"))
                        .tooltip(Text.of("Example Category Description"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Boolean Controllers but it has a super long name that needs to wrap"))
                                .tooltip(Text.of("Test!"))
                                .collapsed(true)
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.of("Boolean Toggle"))
                                        .tooltip(Text.of("A simple toggle button."))
                                        .binding(
                                                false,
                                                () -> TestSettings.booleanToggle,
                                                (value) -> TestSettings.booleanToggle = value
                                        )
                                        .controller(BooleanController::new)
                                        .requiresRestart(true)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.of("Custom Boolean Toggle"))
                                        .tooltip(Text.of("You can customize these controllers like this!"))
                                        .binding(
                                                false,
                                                () -> TestSettings.customBooleanToggle,
                                                (value) -> TestSettings.customBooleanToggle = value
                                        )
                                        .controller(opt -> new BooleanController(opt, state -> state ? Text.of("Amazing") : Text.of("Not Amazing"), true))
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.of("Tick Box"))
                                        .tooltip(Text.of("There are even alternate methods of displaying the same data type!"))
                                        .binding(
                                                false,
                                                () -> TestSettings.tickbox,
                                                (value) -> TestSettings.tickbox = value
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Slider Controllers"))
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
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Input Field Controllers"))
                                .option(Option.createBuilder(String.class)
                                        .name(Text.of("Text Option"))
                                        .binding(
                                                "Hello",
                                                () -> TestSettings.textField,
                                                value -> TestSettings.textField = value
                                        )
                                        .controller(StringController::new)
                                        .build())
                                .option(Option.createBuilder(Color.class)
                                        .name(Text.of("Color Option"))
                                        .binding(Binding.immutable(Color.red))
                                        .controller(ColorController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Enum Controllers"))
                                .option(Option.createBuilder(TestSettings.Alphabet.class)
                                        .name(Text.of("Enum Cycler"))
                                        .binding(
                                                TestSettings.Alphabet.A,
                                                () -> TestSettings.enumOption,
                                                (value) -> TestSettings.enumOption = value
                                        )
                                        .controller(EnumController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Options that aren't really options"))
                                .option(ButtonOption.createBuilder()
                                        .name(Text.of("Button \"Option\""))
                                        .action(screen -> SystemToast.add(MinecraftClient.getInstance().getToastManager(), SystemToast.Type.TUTORIAL_HINT, Text.of("Button Pressed"), Text.of("Button option was invoked!")))
                                        .controller(ActionController::new)
                                        .build())
                                .option(Option.createBuilder(Text.class)
                                        .binding(Binding.immutable(Text.of("Labels that are very large get wrapped around onto a new line! I hope this is a good demonstration!")))
                                        .controller(LabelController::new)
                                        .build())
                                .build())
//                        .group(OptionGroup.createBuilder()
//                                .name(Text.of("Minecraft Bindings"))
//                                .tooltip(Text.of("YACL can also bind Minecraft options!"))
//                                .option(Option.createBuilder(boolean.class)
//                                        .name(Text.of("Minecraft AutoJump"))
//                                        .tooltip(Text.of("You can even bind minecraft options!"))
//                                        .binding(Binding.minecraft(MinecraftClient.getInstance().options.getAutoJump()))
//                                        .controller(TickBoxController::new)
//                                        .build())
//                                .option(Option.createBuilder(GraphicsMode.class)
//                                        .name(Text.of("Minecraft Graphics Mode"))
//                                        .binding(Binding.minecraft(MinecraftClient.getInstance().options.getGraphicsMode()))
//                                        .controller(EnumController::new)
//                                        .build())
//                                .build())
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
                .serializer(opt -> new GsonYACLSerializer(opt, FabricLoader.getInstance().getConfigDir().resolve("yacl.json")))
                .build();
    }
}
