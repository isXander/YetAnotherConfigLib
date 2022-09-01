package dev.isxander.yacl.api.utils;

import dev.isxander.yacl.impl.utils.DimensionIntegerImpl;

public interface Dimension<T extends Number> {
    T x();
    T y();

    T width();
    T height();

    T xLimit();
    T yLimit();

    T centerX();
    T centerY();

    boolean isPointInside(T x, T y);

    Dimension<T> clone();

    Dimension<T> setX(T x);
    Dimension<T> setY(T y);
    Dimension<T> setWidth(T width);
    Dimension<T> setHeight(T height);

    Dimension<T> withX(T x);
    Dimension<T> withY(T y);
    Dimension<T> withWidth(T width);
    Dimension<T> withHeight(T height);

    Dimension<T> move(T x, T y);
    Dimension<T> expand(T width, T height);

    Dimension<T> moved(T x, T y);
    Dimension<T> expanded(T width, T height);

    static Dimension<Integer> ofInt(int x, int y, int width, int height) {
        return new DimensionIntegerImpl(x, y, width, height);
    }
}
