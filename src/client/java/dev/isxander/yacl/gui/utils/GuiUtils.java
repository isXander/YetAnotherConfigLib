package dev.isxander.yacl.gui.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

public class GuiUtils {
    public static MutableText translatableFallback(String key, Text fallback) {
        if (Language.getInstance().hasTranslation(key))
            return Text.translatable(key);
        return fallback.copy();
    }

    public static void enableScissor(int x, int y, int width, int height) {
        Window window = MinecraftClient.getInstance().getWindow();
        double d = window.getScaleFactor();
        RenderSystem.enableScissor((int)(x * d), (int)((window.getScaledHeight() - y - height) * d), (int)(width * d), (int)(height * d));
    }

    public static String shortenString(String string, TextRenderer textRenderer, int maxWidth, String suffix) {
        boolean firstIter = true;
        while (textRenderer.getWidth(string) > maxWidth) {
            string = string.substring(0, Math.max(string.length() - 1 - (firstIter ? 1 : suffix.length() + 1), 0)).trim();
            string += suffix;

            if (string.equals(suffix))
                break;

            firstIter = false;
        }

        return string;
    }
}
