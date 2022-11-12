package dev.isxander.yacl.test.config;

import java.awt.*;

public class ConfigData {
    public boolean booleanToggle = false;
    public boolean customBooleanToggle = false;
    public boolean tickbox = false;
    public int intSlider = 0;
    public double doubleSlider = 0;
    public float floatSlider = 0;
    public long longSlider = 0;
    public String textField = "Hello";
    public Color colorOption = Color.red;
    public Alphabet enumOption = Alphabet.A;

    public boolean groupTestRoot = false;
    public boolean groupTestFirstGroup = false;
    public boolean groupTestFirstGroup2 = false;
    public boolean groupTestSecondGroup = false;

    public int scrollingSlider = 0;

    public enum Alphabet {
        A, B, C
    }
}
