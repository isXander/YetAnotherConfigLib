package dev.isxander.yacl3.test;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.RequireRestartScreen;
import dev.isxander.yacl3.gui.controllers.*;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl3.gui.controllers.slider.FloatSliderController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import dev.isxander.yacl3.gui.controllers.slider.LongSliderController;
import dev.isxander.yacl3.gui.controllers.string.StringController;
import dev.isxander.yacl3.gui.controllers.string.number.DoubleFieldController;
import dev.isxander.yacl3.gui.controllers.string.number.FloatFieldController;
import dev.isxander.yacl3.gui.controllers.string.number.IntegerFieldController;
import dev.isxander.yacl3.gui.controllers.string.number.LongFieldController;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GuiTest {
    public static Screen getModConfigScreenFactory(Screen parent) {
        return YetAnotherConfigLib.create(ConfigTest.GSON, (defaults, config, builder) -> builder
                        .title(Component.literal("Test Suites"))
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Suites"))
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Full Test Suite"))
                                        .action((screen, opt) -> Minecraft.getInstance().setScreen(getFullTestSuite(screen)))
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Component.literal("Auto-gen test"))
                                        .action((screen, opt) -> {
                                            AutogenConfigTest.INSTANCE.load();
                                            Minecraft.getInstance().setScreen(AutogenConfigTest.INSTANCE.generateGui().generateScreen(screen));
                                        })
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Wiki"))
                                        .option(ButtonOption.createBuilder()
                                                .name(Component.literal("Get Started"))
                                                .action((screen, opt) -> Minecraft.getInstance().setScreen(getWikiGetStarted(screen)))
                                                .build())
                                        .build())
                                .build())
                )
                .generateScreen(parent);
    }

    private static Screen getFullTestSuite(Screen parent) {
        AtomicReference<Option<Boolean>> booleanOption = new AtomicReference<>();

        ConfigTest.GSON.serializer().load();
        return YetAnotherConfigLib.create(ConfigTest.GSON, (defaults, config, builder) -> builder
                        .title(Component.literal("Test GUI"))
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Control Examples"))
                                .tooltip(Component.literal("Example Category Description"))
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Boolean Controllers"))
                                        .option(Util.make(() -> {
                                            var opt = Option.<Boolean>createBuilder()
                                                    .name(Component.literal("Boolean Toggle"))
                                                    .description(OptionDescription.createBuilder()
                                                            .text(Component.empty()
                                                                    .append(Component.literal("a").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("a")))))
                                                                    .append(Component.literal("b").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("b")))))
                                                                    .append(Component.literal("c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c c").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("c")))))
                                                                    .append(Component.literal("e").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("e")))))
                                                                    .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://isxander.dev")))
                                                            )
                                                            .webpImage(new ResourceLocation("yacl3", "reach-around-placement.webp"))
                                                            .build())
                                                    .binding(
                                                            defaults.booleanToggle,
                                                            () -> config.booleanToggle,
                                                            (value) -> config.booleanToggle = value
                                                    )
                                                    .controller(BooleanControllerBuilder::create)
                                                    .flag(OptionFlag.GAME_RESTART)
                                                    .build();
                                            booleanOption.set(opt);
                                            return opt;
                                        }))
                                        .option(Option.<Boolean>createBuilder()
                                                .name(Component.literal("Custom Boolean Toggle"))
                                                .description(val -> OptionDescription.createBuilder()
                                                        .text(Component.literal("You can customize controllers like so! YACL is truly infinitely customizable! This tooltip is long in order to demonstrate the cool, smooth scrolling of these descriptions. Did you know, they are also super clickable?! I know, cool right, YACL 3.x really is amazing."))
                                                        .image(Path.of("D:\\Xander\\Downloads\\_MG_0860-Enhanced-NR.png"), new ResourceLocation("yacl", "f.webp")) // TODO: Add img file to git?
                                                        .build())
                                                .binding(
                                                        defaults.customBooleanToggle,
                                                        () -> config.customBooleanToggle,
                                                        (value) -> config.customBooleanToggle = value
                                                )
                                                .controller(opt -> BooleanControllerBuilder.create(opt)
                                                        .formatValue(state -> state ? Component.literal("Amazing") : Component.literal("Not Amazing"))
                                                        .coloured(true))
                                                .listener((opt, val) -> booleanOption.get().setAvailable(val))
                                                .build())
                                        .option(Option.<Boolean>createBuilder()
                                                .name(Component.literal("Tick Box"))
                                                .description(OptionDescription.of(Component.literal("There are even alternate methods of displaying the same data type!")))
                                                .binding(
                                                        defaults.tickbox,
                                                        () -> config.tickbox,
                                                        (value) -> config.tickbox = value
                                                )
                                                .controller(TickBoxControllerBuilder::create)
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Slider Controllers"))
                                        .option(Option.<Integer>createBuilder()
                                                .name(Component.literal("Int Slider"))
                                                .instant(true)
                                                .binding(
                                                        defaults.intSlider,
                                                        () -> config.intSlider,
                                                        value -> config.intSlider = value

                                                )
                                                .customController(opt -> new IntegerSliderController(opt, 0, 3, 1))
                                                .build())
                                        .option(Option.<Double>createBuilder()
                                                .name(Component.literal("Double Slider"))
                                                .binding(
                                                        defaults.doubleSlider,
                                                        () -> config.doubleSlider,
                                                        (value) -> config.doubleSlider = value
                                                )
                                                .customController(opt -> new DoubleSliderController(opt, 0, 3, 0.05))
                                                .build())
                                        .option(Option.<Float>createBuilder()
                                                .name(Component.literal("Float Slider"))
                                                .binding(
                                                        defaults.floatSlider,
                                                        () -> config.floatSlider,
                                                        (value) -> config.floatSlider = value
                                                )
                                                .customController(opt -> new FloatSliderController(opt, 0, 3, 0.1f))
                                                .build())
                                        .option(Option.<Long>createBuilder()
                                                .name(Component.literal("Long Slider"))
                                                .binding(
                                                        defaults.longSlider,
                                                        () -> config.longSlider,
                                                        (value) -> config.longSlider = value
                                                )
                                                .customController(opt -> new LongSliderController(opt, 0, 1_000_000, 100))
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Input Field Controllers"))
                                        .option(Option.<String>createBuilder()
                                                .name(Component.literal("Component Option"))
                                                .binding(
                                                        defaults.textField,
                                                        () -> config.textField,
                                                        value -> config.textField = value
                                                )
                                                .customController(StringController::new)
                                                .build())
                                        .option(Option.<Color>createBuilder()
                                                .name(Component.literal("Color Option"))
                                                .binding(
                                                        defaults.colorOption,
                                                        () -> config.colorOption,
                                                        value -> config.colorOption = value
                                                )
                                                .customController(ColorController::new)
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Number Fields"))
                                        .option(Option.<Double>createBuilder()
                                                .name(Component.literal("Double Field"))
                                                .binding(
                                                        defaults.doubleField,
                                                        () -> config.doubleField,
                                                        value -> config.doubleField = value
                                                )
                                                .customController(DoubleFieldController::new)
                                                .build())
                                        .option(Option.<Float>createBuilder()
                                                .name(Component.literal("Float Field"))
                                                .binding(
                                                        defaults.floatField,
                                                        () -> config.floatField,
                                                        value -> config.floatField = value
                                                )
                                                .customController(FloatFieldController::new)
                                                .build())
                                        .option(Option.<Integer>createBuilder()
                                                .name(Component.literal("Integer Field"))
                                                .binding(
                                                        defaults.intField,
                                                        () -> config.intField,
                                                        value -> config.intField = value
                                                )
                                                .customController(IntegerFieldController::new)
                                                .build())
                                        .option(Option.<Long>createBuilder()
                                                .name(Component.literal("Long Field"))
                                                .binding(
                                                        defaults.longField,
                                                        () -> config.longField,
                                                        value -> config.longField = value
                                                )
                                                .customController(LongFieldController::new)
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Enum Controllers"))
                                        .option(Option.<ConfigTest.Alphabet>createBuilder()
                                                .name(Component.literal("Enum Cycler"))
                                                .binding(
                                                        defaults.enumOption,
                                                        () -> config.enumOption,
                                                        (value) -> config.enumOption = value
                                                )
                                                .customController(opt -> new EnumController<>(opt, ConfigTest.Alphabet.class))
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Dropdown Controllers"))
                                        .option(Option.<String>createBuilder()
                                                .name(Component.literal("String Dropdown"))
                                                .binding(
                                                        defaults.stringOptions,
                                                        () -> config.stringOptions,
                                                        (value) -> config.stringOptions = value
                                                )
                                                .controller(opt -> DropdownStringControllerBuilder.create(opt)
                                                        .values("Apple", "Banana", "Cherry", "Date")
                                                )
                                                .build())
                                        .option(Option.<String>createBuilder()
                                                .name(Component.literal("String suggestions"))
                                                .binding(
                                                        defaults.stringSuggestions,
                                                        () -> config.stringSuggestions,
                                                        (value) -> config.stringSuggestions = value
                                                )
                                                .controller(opt -> DropdownStringControllerBuilder.create(opt)
                                                        .values("Apple", "Banana", "Cherry", "Date")
                                                        .allowAnyValue(true)
                                                )
                                                .build())
                                        .option(Option.<Item>createBuilder()
                                                .name(Component.literal("Item Dropdown"))
                                                .binding(
                                                        defaults.item,
                                                        () -> config.item,
                                                        (value) -> config.item = value
                                                )
                                                .controller(ItemControllerBuilder::create)
                                                .build())
                                        .option(Option.<ChatFormatting>createBuilder()
                                                .name(Component.literal("Enum Dropdown"))
                                                .binding(
                                                        defaults.formattingOption,
                                                        () -> config.formattingOption,
                                                        (value) -> config.formattingOption = value
                                                )
                                                .controller(option -> EnumDropdownControllerBuilder.create(option).formatValue(formatting -> Component.literal(StringUtils.capitalize(formatting.getName()).replaceAll("_", " "))))
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("Options that aren't really options"))
                                        .option(ButtonOption.createBuilder()
                                                .name(Component.literal("Button \"Option\""))
                                                .action((screen, opt) -> SystemToast.add(Minecraft.getInstance().getToasts(), SystemToast.SystemToastIds.TUTORIAL_HINT, Component.literal("Button Pressed"), Component.literal("Button option was invoked!")))
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
                                        .description(OptionDescription.of(Component.literal("YACL can also bind Minecraft options!")))
                                        .option(Option.<Boolean>createBuilder()
                                                .name(Component.literal("Minecraft AutoJump"))
                                                .description(OptionDescription.of(Component.literal("You can even bind minecraft options!")))
                                                .binding(Binding.minecraft(Minecraft.getInstance().options.autoJump()))
                                                .customController(TickBoxController::new)
                                                .build())
                                        .option(Option.<GraphicsStatus>createBuilder()
                                                .name(Component.literal("Minecraft Graphics Mode"))
                                                .binding(Binding.minecraft(Minecraft.getInstance().options.graphicsMode()))
                                                .customController(opt -> new EnumController<>(opt, GraphicsStatus.class))
                                                .build())
                                        .build())
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("List Test"))
                                .group(ListOption.<String>createBuilder()
                                        .name(Component.literal("String List"))
                                        .binding(
                                                defaults.stringList,
                                                () -> config.stringList,
                                                val -> config.stringList = val
                                        )
                                        .controller(StringControllerBuilder::create)
                                        .initial("")
                                        .minimumNumberOfEntries(3)
                                        .maximumNumberOfEntries(5)
                                        .insertEntriesAtEnd(true)
                                        .build())
                                .group(ListOption.<Integer>createBuilder()
                                        .name(Component.literal("Slider List"))
                                        .binding(
                                                defaults.intList,
                                                () -> config.intList,
                                                val -> config.intList = val
                                        )
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                                .range(0, 10).step(1))
                                        .initial(0)
                                        .available(false)
                                        .build())
                                .group(ListOption.<Component>createBuilder()
                                        .name(Component.literal("Useless Label List"))
                                        .binding(Binding.immutable(List.of(Component.literal("It's quite impressive that literally every single controller works, without problem."))))
                                        .customController(LabelController::new)
                                        .initial(Component.literal("Initial label"))
                                        .build())
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Group Test"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Root Test"))
                                        .binding(
                                                defaults.groupTestRoot,
                                                () -> config.groupTestRoot,
                                                value -> config.groupTestRoot = value
                                        )
                                        .customController(TickBoxController::new)
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.literal("First Group"))
                                        .option(Option.<Boolean>createBuilder()
                                                .name(Component.literal("First Group Test 1"))
                                                .binding(
                                                        defaults.groupTestFirstGroup,
                                                        () -> config.groupTestFirstGroup,
                                                        value -> config.groupTestFirstGroup = value
                                                )
                                                .customController(TickBoxController::new)
                                                .build())
                                        .option(Option.<Boolean>createBuilder()
                                                .name(Component.literal("First Group Test 2"))
                                                .binding(
                                                        defaults.groupTestFirstGroup2,
                                                        () -> config.groupTestFirstGroup2,
                                                        value -> config.groupTestFirstGroup2 = value
                                                )
                                                .customController(TickBoxController::new)
                                                .build())
                                        .build())
                                .group(OptionGroup.createBuilder()
                                        .name(Component.empty())
                                        .option(Option.<Boolean>createBuilder()
                                                .name(Component.literal("Second Group Test"))
                                                .binding(
                                                        defaults.groupTestSecondGroup,
                                                        () -> config.groupTestSecondGroup,
                                                        value -> config.groupTestSecondGroup = value
                                                )
                                                .customController(TickBoxController::new)
                                                .build())
                                        .build())
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .option(LabelOption.create(Component.literal("This is a test category!")))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .option(LabelOption.create(Component.literal("This is a test category!")))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .option(LabelOption.create(Component.literal("This is a test category!")))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
                                .option(LabelOption.create(Component.literal("This is a test category!")))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .option(LabelOption.create(Component.literal("This is a test category!")))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .option(LabelOption.create(Component.literal("This is a test category!")))
                                .build())
                        .category(ConfigCategory.createBuilder()
                                .name(Component.literal("Category Test"))
                                .option(LabelOption.create(Component.literal("This is a test category!")))
                                .build())
                        .category(PlaceholderCategory.createBuilder()
                                .name(Component.literal("Placeholder Category"))
                                .screen((client, yaclScreen) -> new RequireRestartScreen(yaclScreen))
                                .build())
                        .save(() -> {
                            Minecraft.getInstance().options.save();
                            ConfigTest.GSON.serializer().save();
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
                                .description(OptionDescription.of(Component.literal("This Component will appear when you hover over the name or focus on the collapse button with Tab.")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Boolean Option"))
                                        .description(OptionDescription.of(Component.literal("This Component will appear as a tooltip when you hover over the option.")))
                                        .binding(true, () -> myBooleanOption, newVal -> myBooleanOption = newVal)
                                        .customController(TickBoxController::new)
                                        .build())
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }
}
