package dev.isxander.yacl3.gui;

import dev.isxander.yacl3.api.controller.ValueFormatter;
import net.minecraft.network.chat.Component;

public final class ValueFormatters {
    public static ValueFormatter<Float> percent(int decimalPlaces) {
        return new PercentFormatter(decimalPlaces);
    }

    public record PercentFormatter(int decimalPlaces) implements ValueFormatter<Float> {
        public PercentFormatter() {
            this(1);
        }

        @Override
        public Component format(Float value) {
            return Component.literal(String.format("%." + decimalPlaces + "f%%", value * 100));
        }
    }
}
