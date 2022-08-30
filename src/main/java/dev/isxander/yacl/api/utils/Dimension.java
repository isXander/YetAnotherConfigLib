package dev.isxander.yacl.api.utils;

import dev.isxander.yacl.impl.utils.DimensionIntegerImpl;

public interface Dimension<T extends Number> {
    T x();
    T y();

    T width();
    T height();

    T xLimit();
    T yLimit();

    boolean isPointInside(T x, T y);

    static Dimension<Integer> ofInt(int x, int y, int width, int height) {
        return new DimensionIntegerImpl(x, y, width, height);
    }
}
