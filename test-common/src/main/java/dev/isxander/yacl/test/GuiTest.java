package dev.isxander.yacl.test;

import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.RequireRestartScreen;
import dev.isxander.yacl.gui.controllers.*;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl.gui.controllers.string.StringController;
import dev.isxander.yacl.gui.controllers.string.number.DoubleFieldController;
import dev.isxander.yacl.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl.gui.controllers.string.number.IntegerFieldController;
import dev.isxander.yacl.gui.controllers.string.number.LongFieldController;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.awt.Color;
import java.util.List;

public class GuiTest {
    public static Screen getModConfigScreenFactory(Screen parent) {
        return YetAnotherConfigLib.create(ConfigTest.GSON, (defaults, config, builder) -> builder
                        .title(Component.literal("Test Suites"))
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Suites"))
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Full Test Suite"))
                                        .controller(ActionController::new)
                                        .action((screen, opt) -> Minecraft.getInstance().setScreen(getFullTestSuite(screen)))
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Wiki"))
                                        .option(ButtonOption.createBuilder()
                                                .name(Component.literal("Get Started"))
                                                .controller(ActionController::new)
                                                .action((screen, opt) -> Minecraft.getInstance().setScreen(getWikiGetStarted(screen)))
                                                .build())
                                        .build())
                                .build())
                )
                .generateScreen(parent);
    }

    private static Screen getFullTestSuite(Screen parent) {
        return YetAnotherConfigLib.create(ConfigTest.GSON, (defaults, config, builder) -> builder
                        .title(Component.literal("Test GUI"))
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Control Examples"))
                                .tooltip(Component.literal("Example Category Description"))
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Boolean Controllers"))
                                        .tooltip(Component.literal("Test!"))
                                        .option(Option.createBuilder(boolean.class)
                                                .name(Component.literal("Boolean Toggle"))
                                                .tooltip(value -> Component.literal("A simple toggle button that contains the value '" + value + "'"))
                                                .binding(
                                                        defaults.booleanToggle,
                                                        () -> config.booleanToggle,
                                                        (value) -> config.booleanToggle = value
                                                )
                                                .controller(BooleanController::new)
                                                .flag(OptionFlag.GAME_RESTART)
                                                .build())
                                        .option(Option.createBuilder(boolean.class)
                                                .name(Component.literal("Custom Boolean Toggle"))
                                                .tooltip(Component.literal("You can customize these controllers like this!"))
                                                .tooltip(Component.empty())
                                                .tooltip(opt -> Component.empty())
                                                .binding(
                                                        defaults.customBooleanToggle,
                                                        () -> config.customBooleanToggle,
                                                        (value) -> config.customBooleanToggle = value
                                                )
                                                .controller(opt -> new BooleanController(opt, state -> state ? Component.literal("Amazing") : Component.literal("Not Amazing"), true))
                                                .build())
                                        .option(Option.createBuilder(boolean.class)
                                                .name(Component.literal("Tick Box"))
                                                .tooltip(Component.literal("There are even alternate methods of displaying the same data type!"))
                                                .binding(
                                                        defaults.tickbox,
                                                        () -> config.tickbox,
                                                        (value) -> config.tickbox = value
                                                )
                                                .controller(TickBoxController::new)
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Slider Controllers"))
                                        .option(Option.createBuilder(int.class)
                                                .name(Component.literal("Int Slider"))
                                                .instant(true)
                                                .binding(
                                                        defaults.intSlider,
                                                        () -> config.intSlider,
                                                        value -> config.intSlider = value

                                                )
                                                .controller(opt -> new IntegerSliderController(opt, 0, 3, 1))
                                                .build())
                                        .option(Option.createBuilder(double.class)
                                                .name(Component.literal("Double Slider"))
                                                .binding(
                                                        defaults.doubleSlider,
                                                        () -> config.doubleSlider,
                                                        (value) -> config.doubleSlider = value
                                                )
                                                .controller(opt -> new DoubleSliderController(opt, 0, 3, 0.05))
                                                .build())
                                        .option(Option.createBuilder(float.class)
                                                .name(Component.literal("Float Slider"))
                                                .binding(
                                                        defaults.floatSlider,
                                                        () -> config.floatSlider,
                                                        (value) -> config.floatSlider = value
                                                )
                                                .controller(opt -> new FloatSliderController(opt, 0, 3, 0.1f))
                                                .build())
                                        .option(Option.createBuilder(long.class)
                                                .name(Component.literal("Long Slider"))
                                                .binding(
                                                        defaults.longSlider,
                                                        () -> config.longSlider,
                                                        (value) -> config.longSlider = value
                                                )
                                                .controller(opt -> new LongSliderController(opt, 0, 1_000_000, 100))
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Input Field Controllers"))
                                        .option(Option.createBuilder(String.class)
                                                .name(Component.literal("Component Option"))
                                                .binding(
                                                        defaults.textField,
                                                        () -> config.textField,
                                                        value -> config.textField = value
                                                )
                                                .controller(StringController::new)
                                                .build())
                                        .option(Option.createBuilder(Color.class)
                                                .name(Component.literal("Color Option"))
                                                .binding(
                                                        defaults.colorOption,
                                                        () -> config.colorOption,
                                                        value -> config.colorOption = value
                                                )
                                                .controller(ColorController::new)
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Number Fields"))
                                        .option(Option.createBuilder(double.class)
                                                .name(Component.literal("Double Field"))
                                                .binding(
                                                        defaults.doubleField,
                                                        () -> config.doubleField,
                                                        value -> config.doubleField = value
                                                )
                                                .controller(DoubleFieldController::new)
                                                .build())
                                        .option(Option.createBuilder(float.class)
                                                .name(Component.literal("Float Field"))
                                                .binding(
                                                        defaults.floatField,
                                                        () -> config.floatField,
                                                        value -> config.floatField = value
                                                )
                                                .controller(FloatFieldController::new)
                                                .build())
                                        .option(Option.createBuilder(int.class)
                                                .name(Component.literal("Integer Field"))
                                                .binding(
                                                        defaults.intField,
                                                        () -> config.intField,
                                                        value -> config.intField = value
                                                )
                                                .controller(IntegerFieldController::new)
                                                .build())
                                        .option(Option.createBuilder(long.class)
                                                .name(Component.literal("Long Field"))
                                                .binding(
                                                        defaults.longField,
                                                        () -> config.longField,
                                                        value -> config.longField = value
                                                )
                                                .controller(LongFieldController::new)
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Enum Controllers"))
                                        .option(Option.createBuilder(ConfigTest.Alphabet.class)
                                                .name(Component.literal("Enum Cycler"))
                                                .binding(
                                                        defaults.enumOption,
                                                        () -> config.enumOption,
                                                        (value) -> config.enumOption = value
                                                )
                                                .controller(EnumController::new)
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Options that aren't really options"))
                                        .option(ButtonOption.createBuilder()
                                                .name(Component.literal("Button \"Option\""))
                                                .action((screen, opt) -> SystemToast.add(Minecraft.getInstance().getToasts(), SystemToast.SystemToastIds.TUTORIAL_HINT, Component.literal("Button Pressed"), Component.literal("Button option was invoked!")))
                                                .controller(ActionController::new)
                                                .build())
                                        .option(LabelOption.create(
                                                Component.empty()
                                                        .append(Component.literal("a").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("a")))))
                                                        .append(Component.literal("b").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("b")))))
                                                        .append(Component.literal("c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("c")))))
                                                        .append(Component.literal("e").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("e")))))
                                                        .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://isxander.dev"))))
                                        )
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Minecraft Bindings"))
                                        .tooltip(Component.literal("YACL can also bind Minecraft options!"))
                                        .option(Option.createBuilder(boolean.class)
                                                .name(Component.literal("Minecraft AutoJump"))
                                                .tooltip(Component.literal("You can even bind minecraft options!"))
                                                .binding(Binding.minecraft(Minecraft.getInstance().options.autoJump()))
                                                .controller(TickBoxController::new)
                                                .build())
                                        .option(Option.createBuilder(GraphicsStatus.class)
                                                .name(Component.literal("Minecraft Graphics Mode"))
                                                .binding(Binding.minecraft(Minecraft.getInstance().options.graphicsMode()))
                                                .controller(EnumController::new)
                                                .build())
                                        .build())
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("List Test"))
                                .group(ListOption.createBuilder(String.class)
                                        .name(Component.literal("String List"))
                                        .binding(
                                                defaults.stringList,
                                                () -> config.stringList,
                                                val -> config.stringList = val
                                        )
                                        .controller(StringController::new)
                                        .initial("")
                                        .build())
                                .group(ListOption.createBuilder(Integer.class)
                                        .name(Component.literal("Slider List"))
                                        .binding(
                                                defaults.intList,
                                                () -> config.intList,
                                                val -> config.intList = val
                                        )
                                        .controller(opt -> new IntegerSliderController(opt, 0, 10, 1))
                                        .initial(0)
                                        .available(false)
                                        .build())
                                .group(ListOption.createBuilder(Component.class)
                                        .name(Component.literal("Useless Label List"))
                                        .binding(Binding.immutable(List.of(Component.literal("It's quite impressive that literally every single controller works, without problem."))))
                                        .controller(LabelController::new)
                                        .initial(Component.literal("Initial label"))
                                        .build())
                                .build())
                        .category(PlaceholderCategory.createBuilder()
                                .name(Component.literal("Placeholder Category"))
                                .screen((client, yaclScreen) -> new RequireRestartScreen(yaclScreen))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Group Test"))
                                .option(Option.createBuilder(boolean.class)
                                        .name(Component.literal("Root Test"))
                                        .binding(
                                                defaults.groupTestRoot,
                                                () -> config.groupTestRoot,
                                                value -> config.groupTestRoot = value
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("First Group"))
                                        .option(Option.createBuilder(boolean.class)
                                                .name(Component.literal("First Group Test 1"))
                                                .binding(
                                                        defaults.groupTestFirstGroup,
                                                        () -> config.groupTestFirstGroup,
                                                        value -> config.groupTestFirstGroup = value
                                                )
                                                .controller(TickBoxController::new)
                                                .build())
                                        .option(Option.createBuilder(boolean.class)
                                                .name(Component.literal("First Group Test 2"))
                                                .binding(
                                                        defaults.groupTestFirstGroup2,
                                                        () -> config.groupTestFirstGroup2,
                                                        value -> config.groupTestFirstGroup2 = value
                                                )
                                                .controller(TickBoxController::new)
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.empty())
                                        .option(Option.createBuilder(boolean.class)
                                                .name(Component.literal("Second Group Test"))
                                                .binding(
                                                        defaults.groupTestSecondGroup,
                                                        () -> config.groupTestSecondGroup,
                                                        value -> config.groupTestSecondGroup = value
                                                )
                                                .controller(TickBoxController::new)
                                                .build())
                                        .build())
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Scroll Test"))
                                .option(Option.createBuilder(int.class)
                                        .name(Component.literal("Int Slider that is cut off because the slider"))
                                        .binding(
                                                defaults.scrollingSlider,
                                                () -> config.scrollingSlider,
                                                (value) -> config.scrollingSlider = value
                                        )
                                        .controller(opt -> new IntegerSliderController(opt, 0, 10, 1))
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Option"))
                                        .action((screen, opt) -> {
                                        })
                                        .controller(ActionController::new)
                                        .build())
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .build())
                        .save(() -> {
                            Minecraft.getInstance().options.save();
                            ConfigTest.GSON.save();
                        })
                )
                .generateScreen(parent);
    }

    private static boolean myBooleanOption = true;

    private static Screen getWikiGetStarted(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Used for narration. Could be used to render a title in the future."))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Name of the category"))
                        .tooltip(Component.literal("This Component will appear as a tooltip when you hover or focus the button with Tab. There is no need to add \n to wrap as YACL will do it for you."))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Name of the group"))
                                .tooltip(Component.literal("This Component will appear when you hover over the name or focus on the collapse button with Tab."))
                                .option(Option.createBuilder(boolean.class)
                                        .name(Component.literal("Boolean Option"))
                                        .tooltip(Component.literal("This Component will appear as a tooltip when you hover over the option."))
                                        .binding(true, () -> myBooleanOption, newVal -> myBooleanOption = newVal)
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }
}
