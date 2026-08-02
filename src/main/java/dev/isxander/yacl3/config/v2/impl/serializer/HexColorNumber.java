package dev.isxander.yacl3.config.v2.impl.serializer;

import java.util.HexFormat;

public class HexColorNumber extends Number {
    private static final HexFormat HEX_FORMAT = HexFormat.of().withPrefix("0x").withUpperCase();
    protected final int value;

    public HexColorNumber(int value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexColorNumber other)) return false;
        return value == other.value;
    }

    @Override
    public String toString() {
        return HEX_FORMAT.toHexDigits(value);
    }
}
