package dev.isxander.yacl.test.config;

import dev.isxander.yacl.config.ConfigEntry;

import java.awt.*;

public class ConfigData {
    @ConfigEntry public boolean booleanToggle = false;
    @ConfigEntry public boolean customBooleanToggle = false;
    @ConfigEntry public boolean tickbox = false;
    @ConfigEntry public int intSlider = 0;
    @ConfigEntry public double doubleSlider = 0;
    @ConfigEntry public float floatSlider = 0;
    @ConfigEntry public long longSlider = 0;
    @ConfigEntry public String textField = "Hello";
    @ConfigEntry public Color colorOption = Color.red;
    @ConfigEntry public Alphabet enumOption = Alphabet.A;

    @ConfigEntry public boolean groupTestRoot = false;
    @ConfigEntry public boolean groupTestFirstGroup = false;
    @ConfigEntry public boolean groupTestFirstGroup2 = false;
    @ConfigEntry public boolean groupTestSecondGroup = false;

    @ConfigEntry public int scrollingSlider = 0;

    public enum Alphabet {
        A, B, C
    }
}
