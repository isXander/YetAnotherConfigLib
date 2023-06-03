package dev.isxander.yacl3.api.utils;

public interface MutableDimension<T extends Number> extends Dimension<T> {
    MutableDimension<T> setX(T x);
    MutableDimension<T> setY(T y);
    MutableDimension<T> setWidth(T width);
    MutableDimension<T> setHeight(T height);

    MutableDimension<T> move(T x, T y);
    MutableDimension<T> expand(T width, T height);
}
