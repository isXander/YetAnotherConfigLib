package dev.isxander.yacl.impl.utils;

import dev.isxander.yacl.api.utils.Dimension;

public class DimensionIntegerImpl implements Dimension<Integer> {
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
    public Dimension<Integer> clone() {
        return new DimensionIntegerImpl(x, y, width, height);
    }

    @Override public Dimension<Integer> setX(Integer x) { this.x = x; return this; }
    @Override public Dimension<Integer> setY(Integer y) { this.y = y; return this; }
    @Override public Dimension<Integer> setWidth(Integer width) { this.width = width; return this; }
    @Override public Dimension<Integer> setHeight(Integer height) { this.height = height; return this; }

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
        return clone().withWidth(width);
    }

    @Override
    public Dimension<Integer> withHeight(Integer height) {
        return clone().withHeight(height);
    }

    @Override
    public Dimension<Integer> move(Integer x, Integer y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public Dimension<Integer> expand(Integer width, Integer height) {
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
