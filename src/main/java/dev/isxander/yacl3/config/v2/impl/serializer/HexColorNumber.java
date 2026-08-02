package dev.isxander.yacl3.config.v2.impl.serializer;

public class HexColorNumber extends Number {
    private final int value;

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
        return String.format("0x%08X", value);
    }
}
