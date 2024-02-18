package dev.isxander.yacl3.test;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class ConfigTest {
    public static final ConfigClassHandler<ConfigTest> GSON = ConfigClassHandler.createBuilder(ConfigTest.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("yacl-test.json"))
                    .build())
            .build();

    @SerialEntry
    public boolean booleanToggle = false;
    @SerialEntry
    public boolean customBooleanToggle = false;
    @SerialEntry
    public boolean tickbox = false;
    @SerialEntry
    public int intSlider = 0;
    @SerialEntry
    public double doubleSlider = 0;
    @SerialEntry
    public float floatSlider = 0;
    @SerialEntry
    public long longSlider = 0;
    @SerialEntry
    public String textField = "Hello";
    @SerialEntry
    public Color colorOption = Color.red;
    @SerialEntry
    public double doubleField = 0.5;
    @SerialEntry
    public float floatField = 0.5f;
    @SerialEntry
    public int intField = 5;
    @SerialEntry
    public long longField = 5;
    @SerialEntry
    public Alphabet enumOption = Alphabet.A;
    @SerialEntry
    public String stringOptions = "Banana";
    @SerialEntry
    public String stringSuggestions = "";
    @SerialEntry
    public Item item = Items.OAK_LOG;

    @SerialEntry
    public List<String> stringList = List.of("This is quite cool.", "You can add multiple items!", "And it is integrated so well into Option groups!");
    @SerialEntry
    public List<Integer> intList = List.of(1, 2, 3);

    @SerialEntry
    public Map<String, String> stringMap = Map.of("Key", "Value", "This is a map!", "Fully integrated just like lists.");

    @SerialEntry
    public boolean groupTestRoot = false;
    @SerialEntry
    public boolean groupTestFirstGroup = false;
    @SerialEntry
    public boolean groupTestFirstGroup2 = false;
    @SerialEntry
    public boolean groupTestSecondGroup = false;

    @SerialEntry
    public int scrollingSlider = 0;

    public enum Alphabet {
        A, B, C
    }
}
