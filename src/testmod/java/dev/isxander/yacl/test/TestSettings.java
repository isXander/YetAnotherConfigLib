package dev.isxander.yacl.test;

import java.awt.*;

public class TestSettings {
    public static final TestSettings INSTANCE = new TestSettings();

    public boolean booleanToggle = false;
    public boolean customBooleanToggle = false;
    public boolean tickbox = false;
    public int intSlider = 0;
    public double doubleSlider = 0;
    public float floatSlider = 0;
    public long longSlider = 0;
    public String textField = "Hello";
    public Color colorField = Color.red;
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
