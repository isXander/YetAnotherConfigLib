package dev.isxander.yacl.test;

import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.*;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import dev.isxander.yacl.impl.MinecraftOptionsStorage;
import dev.isxander.yacl.serialization.impl.GsonYACLSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
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
        for (Storage<?> storage : yacl.storages()) {
            storage.load();
        }
        yacl.serializer().load();
    }

    public static ClientEntrypoint getInstance() {
        return instance;
    }

    public YetAnotherConfigLib getYACL() {
        return yacl;
    }
    
    private void makeYacl() {
        GsonYACLSerializer<TestSettings> serializer = new GsonYACLSerializer<>(() -> yacl, TestSettings.INSTANCE, FabricLoader.getInstance().getConfigDir().resolve("yacl.json"));

        yacl = YetAnotherConfigLib.createBuilder()
                .title(Text.of("Test GUI"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Control Examples"))
                        .tooltip(Text.of("Example Category Description"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Boolean Controllers but it has a super long name that needs to wrap"))
                                .tooltip(Text.of("Test!"))
                                .collapsed(true)
                                .option(Option.createBuilder(boolean.class, serializer)
                                        .name(Text.of("Boolean Toggle"))
                                        .tooltip(Text.of("A simple toggle button."))
                                        .binding(
                                                opts -> false,
                                                opts -> opts.booleanToggle,
                                                (opts, value) -> opts.booleanToggle = value
                                        )
                                        .controller(BooleanController::new)
                                        .requiresRestart(true)
                                        .build())
                                .option(Option.createBuilder(boolean.class, serializer)
                                        .name(Text.of("Custom Boolean Toggle"))
                                        .tooltip(Text.of("You can customize these controllers like this!"))
                                        .binding(
                                                opts -> false,
                                                opts -> opts.customBooleanToggle,
                                                (opts, value) -> opts.customBooleanToggle = value
                                        )
                                        .controller(opt -> new BooleanController(opt, state -> state ? Text.of("Amazing") : Text.of("Not Amazing"), true))
                                        .build())
                                .option(Option.createBuilder(boolean.class, serializer)
                                        .name(Text.of("Tick Box"))
                                        .tooltip(Text.of("There are even alternate methods of displaying the same data type!"))
                                        .binding(
                                                opts -> false,
                                                opts -> opts.tickbox,
                                                (opts, value) -> opts.tickbox = value
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Slider Controllers"))
                                .option(Option.createBuilder(int.class, serializer)
                                        .name(Text.of("Int Slider that is cut off because the slider"))
                                        .binding(
                                                opts -> 0,
                                                opts -> opts.intSlider,
                                                (opts, value) -> opts.intSlider = value
                                        )
                                        .controller(opt -> new IntegerSliderController(opt, 0, 3, 1))
                                        .build())
                                .option(Option.createBuilder(double.class, serializer)
                                        .name(Text.of("Double Slider"))
                                        .binding(
                                                opts -> 0.0,
                                                opts -> opts.doubleSlider,
                                                (opts, value) -> opts.doubleSlider = value
                                        )
                                        .controller(opt -> new DoubleSliderController(opt, 0, 3, 0.05))
                                        .build())
                                .option(Option.createBuilder(float.class, serializer)
                                        .name(Text.of("Float Slider"))
                                        .binding(
                                                opts -> 0f,
                                                opts -> opts.floatSlider,
                                                (opts, value) -> opts.floatSlider = value
                                        )
                                        .controller(opt -> new FloatSliderController(opt, 0, 3, 0.1f))
                                        .build())
                                .option(Option.createBuilder(long.class, serializer)
                                        .name(Text.of("Long Slider"))
                                        .binding(
                                                opts -> 0L,
                                                opts -> opts.longSlider,
                                                (opts, value) -> opts.longSlider = value
                                        )
                                        .controller(opt -> new LongSliderController(opt, 0, 1_000_000, 100))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Input Field Controllers"))
                                .option(Option.createBuilder(String.class, serializer)
                                        .name(Text.of("Text Option"))
                                        .binding(
                                                opts -> "Hello",
                                                opts -> opts.textField,
                                                (opts, value) -> opts.textField = value
                                        )
                                        .controller(StringController::new)
                                        .build())
                                .option(Option.createBuilder(Color.class, serializer)
                                        .name(Text.of("Color Option"))
                                        .binding(
                                                opts -> Color.red,
                                                opts -> opts.colorField,
                                                (opts, value) -> opts.colorField = value
                                        )
                                        .controller(ColorController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Enum Controllers"))
                                .option(Option.createBuilder(TestSettings.Alphabet.class, serializer)
                                        .name(Text.of("Enum Cycler"))
                                        .binding(
                                                opts -> TestSettings.Alphabet.A,
                                                opts -> opts.enumOption,
                                                (opts, value) -> opts.enumOption = value
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
                                .option(Option.createBuilder(Text.class, serializer)
                                        .binding(Binding.immutable(Text.of("Labels that are very large get wrapped around onto a new line! I hope this is a good demonstration!")))
                                        .controller(LabelController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Minecraft Bindings"))
                                .tooltip(Text.of("YACL can also bind Minecraft options!"))
                                .option(Option.createBuilder(boolean.class, MinecraftOptionsStorage.INSTANCE)
                                        .name(Text.of("Minecraft AutoJump"))
                                        .tooltip(Text.of("You can even bind minecraft options!"))
                                        .binding(Binding.minecraft(GameOptions::getAutoJump))
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(GraphicsMode.class, MinecraftOptionsStorage.INSTANCE)
                                        .name(Text.of("Minecraft Graphics Mode"))
                                        .binding(Binding.minecraft(GameOptions::getGraphicsMode))
                                        .controller(EnumController::new)
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Group Test"))
                        .option(Option.createBuilder(boolean.class, serializer)
                                .name(Text.of("Root Test"))
                                .binding(
                                        opts -> false,
                                        opts -> opts.groupTestRoot,
                                        (opts, value) -> opts.groupTestRoot = value
                                )
                                .controller(TickBoxController::new)
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("First Group"))
                                .option(Option.createBuilder(boolean.class, serializer)
                                        .name(Text.of("First Group Test 1"))
                                        .binding(
                                                opts -> false,
                                                opts -> opts.groupTestFirstGroup,
                                                (opts, value) -> opts.groupTestFirstGroup = value
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class, serializer)
                                        .name(Text.of("First Group Test 2"))
                                        .binding(
                                                opts -> false,
                                                opts -> opts.groupTestFirstGroup2,
                                                (opts, value) -> opts.groupTestFirstGroup2 = value
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.empty())
                                .option(Option.createBuilder(boolean.class, serializer)
                                        .name(Text.of("Second Group Test"))
                                        .binding(
                                                opts -> false,
                                                opts -> opts.groupTestSecondGroup,
                                                (opts, value) -> opts.groupTestSecondGroup = value
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .build())
                .build();
    }
}
