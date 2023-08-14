package dev.isxander.yacl3.test;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfigV2Test {
    public static ConfigClassHandler<ConfigV2Test> INSTANCE = ConfigClassHandler.createBuilder(ConfigV2Test.class)
            .id(new ResourceLocation("yacl3", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("yacl-test-v2.json5"))
                    .setJson5(true)
                    .build())
            .autoGen(true)
            .build();

    @AutoGen(category = "test", group = "master_test")
    @MasterTickBox({ "testTickBox", "testInt" })
    @SerialEntry(comment = "This option does this and that which is cool because this...")
    public boolean masterOption = true;

    @AutoGen(category = "test", group = "master_test")
    @TickBox
    @SerialEntry(comment = "This is a cool comment omg this is amazing")
    public boolean testTickBox = true;

    @AutoGen(category = "test", group = "master_test")
    @IntSlider(min = 0, max = 10, step = 2)
    @SerialEntry public int testInt = 0;

    @AutoGen(category = "test", group = "misc") @Label
    private final Component testLabel = Component.literal("Test label");
}
