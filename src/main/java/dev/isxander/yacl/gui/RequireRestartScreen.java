package dev.isxander.yacl.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RequireRestartScreen extends ConfirmScreen {
    public RequireRestartScreen(Screen parent) {
        super(option -> {
            if (option) MinecraftClient.getInstance().scheduleStop();
            else MinecraftClient.getInstance().setScreen(parent);
        }, Text.translatable("yacl.restart.title").formatted(Formatting.RED, Formatting.BOLD), Text.translatable("yacl.restart.message"), Text.translatable("yacl.restart.yes"), Text.translatable("yacl.restart.no"));
    }
}
