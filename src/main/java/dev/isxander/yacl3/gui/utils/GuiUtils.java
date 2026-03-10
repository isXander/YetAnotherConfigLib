package dev.isxander.yacl3.gui.utils;

import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public class GuiUtils {

    public static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            YACLPlatform.mcRl("widget/button"), // normal
            YACLPlatform.mcRl("widget/button_disabled"), // disabled & !focused
            YACLPlatform.mcRl("widget/button_highlighted"), // !disabled & focused
            YACLPlatform.mcRl("widget/slider_highlighted") // disabled & focused
    );

    public static MutableComponent translatableFallback(String key, Component fallback) {
        if (Language.getInstance().has(key))
            return Component.translatable(key);
        return fallback.copy();
    }

    public static String shortenString(String string, Font font, int maxWidth, String suffix) {
        if (string.isEmpty())
            return string;

        boolean firstIter = true;
        while (font.width(string) > maxWidth) {
            string = string.substring(0, Math.max(string.length() - 1 - (firstIter ? 1 : suffix.length() + 1), 0)).trim();
            string += suffix;

            if (string.equals(suffix))
                break;

            firstIter = false;
        }

        return string;
    }

    public static FormattedCharSequence overrideStyle(FormattedCharSequence seq, Style style) {
        return output -> seq.accept((c, s, t) -> output.accept(c, style, t));
    }
}
