package dev.isxander.yacl.impl.utils;

import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.api.utils.MutableDimension;

public class DimensionIntegerImpl implements MutableDimension<Integer> {
    private int x, y;
    private int width, height;

    public DimensionIntegerImpl(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public Integer x() {
        return x;
    }

    @Override
    public Integer y() {
        return y;
    }

    @Override
    public Integer width() {
        return width;
    }

    @Override
    public Integer height() {
        return height;
    }

    @Override
    public Integer xLimit() {
        return x + width;
    }

    @Override
    public Integer yLimit() {
        return y + height;
    }

    @Override
    public Integer centerX() {
        return x + width / 2;
    }

    @Override
    public Integer centerY() {
        return y + height / 2;
    }

    @Override
    public boolean isPointInside(Integer x, Integer y) {
        return x >= x() && x <= xLimit() && y >= y() && y <= yLimit();
    }

    @Override
    public MutableDimension<Integer> clone() {
        return new DimensionIntegerImpl(x, y, width, height);
    }

    @Override public MutableDimension<Integer> setX(Integer x) { this.x = x; return this; }
    @Override public MutableDimension<Integer> setY(Integer y) { this.y = y; return this; }
    @Override public MutableDimension<Integer> setWidth(Integer width) { this.width = width; return this; }
    @Override public MutableDimension<Integer> setHeight(Integer height) { this.height = height; return this; }

    @Override
    public Dimension<Integer> withX(Integer x) {
        return clone().setX(x);
    }

    @Override
    public Dimension<Integer> withY(Integer y) {
        return clone().setY(y);
    }

    @Override
    public Dimension<Integer> withWidth(Integer width) {
        return clone().setWidth(width);
    }

    @Override
    public Dimension<Integer> withHeight(Integer height) {
        return clone().setHeight(height);
    }

    @Override
    public MutableDimension<Integer> move(Integer x, Integer y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public MutableDimension<Integer> expand(Integer width, Integer height) {
        this.width += width;
        this.height += height;
        return this;
    }

    @Override
    public Dimension<Integer> moved(Integer x, Integer y) {
        return clone().move(x, y);
    }

    @Override
    public Dimension<Integer> expanded(Integer width, Integer height) {
        return clone().expand(width, height);
    }
}
