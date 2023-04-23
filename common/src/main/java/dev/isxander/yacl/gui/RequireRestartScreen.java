package dev.isxander.yacl.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RequireRestartScreen extends ConfirmScreen {
    public RequireRestartScreen(Screen parent) {
        super(option -> {
            if (option) Minecraft.getInstance().stop();
            else Minecraft.getInstance().setScreen(parent);
        },
                Component.translatable("yacl.restart.title").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                Component.translatable("yacl.restart.message"),
                Component.translatable("yacl.restart.yes"),
                Component.translatable("yacl.restart.no")
        );
    }
}
