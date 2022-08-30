package dev.isxander.yacl.impl.utils;

import dev.isxander.yacl.api.utils.Dimension;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record DimensionIntegerImpl(Integer x, Integer y, Integer width, Integer height) implements Dimension<Integer> {
    @Override
    public Integer xLimit() {
        return x + width;
    }

    @Override
    public Integer yLimit() {
        return y + height;
    }

    @Override
    public boolean isPointInside(Integer x, Integer y) {
        return x >= x() && x <= xLimit() && y >= y() && y <= yLimit();
    }
}
