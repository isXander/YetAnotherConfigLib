package dev.isxander.yacl3.gui;

import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.minecraft.network.chat.Component;

public final class ValueFormatters {
    public static final PercentFormatter PERCENT = new PercentFormatter();

    public static final class PercentFormatter implements ValueFormatter<Float> {
        @Override
        public Component format(Float value) {
            return Component.literal(String.format("%.0f%%", value * 100));
        }
    }
}
