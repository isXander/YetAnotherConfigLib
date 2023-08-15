package dev.isxander.yacl3.test;

import com.google.common.collect.Lists;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ConfigV2Test {
    public static ConfigClassHandler<ConfigV2Test> INSTANCE = ConfigClassHandler.createBuilder(ConfigV2Test.class)
            .id(new ResourceLocation("yacl3", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("yacl-test-v2.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @AutoGen(category = "test", group = "master_test")
    @MasterTickBox({ "testTickBox", "testInt", "testEnum" })
    @SerialEntry(comment = "This option does this and that which is cool because this...")
    public boolean masterOption = true;

    @AutoGen(category = "test", group = "master_test")
    @TickBox
    @SerialEntry(comment = "This is a cool comment omg this is amazing")
    public boolean testTickBox = true;

    @AutoGen(category = "test", group = "master_test")
    @IntSlider(min = 0, max = 10, step = 2)
    @SerialEntry public int testInt = 0;

    @AutoGen(category = "test", group = "master_test")
    @EnumCycler
    @SerialEntry public Alphabet testEnum = Alphabet.A;

    @AutoGen(category = "test", group = "misc") @Label
    private final Component testLabel = Component.literal("Test label");

    @AutoGen(category = "test")
    @ListGroup(valueFactory = TestListValueFactory.class, controllerFactory = TestListControllerFactory.class)
    @SerialEntry public List<String> testList = Lists.newArrayList("A", "B");

    public enum Alphabet implements NameableEnum {
        A, B, C;

        @Override
        public Component getDisplayName() {
            return Component.literal(name());
        }
    }

    public static class TestListValueFactory implements ListGroup.ValueFactory<String> {
        @Override
        public String provideNewValue() {
            return "";
        }
    }

    public static class TestListControllerFactory implements ListGroup.ControllerFactory<String> {
        @Override
        public ControllerBuilder<String> createController(ListGroup annotation, ConfigField<List<String>> field, OptionAccess storage, Option<String> option) {
            return StringControllerBuilder.create(option);
        }
    }
}
