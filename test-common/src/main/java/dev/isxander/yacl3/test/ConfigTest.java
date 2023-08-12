package dev.isxander.yacl3.test;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigEntry;
import dev.isxander.yacl3.config.v2.api.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;

import java.awt.*;
import java.util.List;

public class ConfigTest {
    public static final ConfigClassHandler<ConfigTest> GSON = ConfigClassHandler.createBuilder(ConfigTest.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("yacl-test.json"))
                    .build())
            .build();

    @ConfigEntry public boolean booleanToggle = false;
    @ConfigEntry public boolean customBooleanToggle = false;
    @ConfigEntry public boolean tickbox = false;
    @ConfigEntry public int intSlider = 0;
    @ConfigEntry public double doubleSlider = 0;
    @ConfigEntry public float floatSlider = 0;
    @ConfigEntry public long longSlider = 0;
    @ConfigEntry public String textField = "Hello";
    @ConfigEntry public Color colorOption = Color.red;
    @ConfigEntry public double doubleField = 0.5;
    @ConfigEntry public float floatField = 0.5f;
    @ConfigEntry public int intField = 5;
    @ConfigEntry public long longField = 5;
    @ConfigEntry public Alphabet enumOption = Alphabet.A;

    @ConfigEntry
    public List<String> stringList = List.of("This is quite cool.", "You can add multiple items!", "And it is integrated so well into Option groups!");
    @ConfigEntry
    public List<Integer> intList = List.of(1, 2, 3);

    @ConfigEntry
    public boolean groupTestRoot = false;
    @ConfigEntry
    public boolean groupTestFirstGroup = false;
    @ConfigEntry
    public boolean groupTestFirstGroup2 = false;
    @ConfigEntry
    public boolean groupTestSecondGroup = false;

    @ConfigEntry
    public int scrollingSlider = 0;

    public enum Alphabet {
        A, B, C
    }
}
