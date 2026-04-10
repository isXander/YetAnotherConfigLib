package dev.isxander.yacl3.gui;

import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RequireRestartScreen extends ConfirmScreen {
    public RequireRestartScreen(Screen parent) {
        super(option -> {
            if (option) Minecraft.getInstance().stop();
            else GuiUtils.setScreen(parent);
        },
                Component.translatable("yacl.restart.title").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                Component.translatable("yacl.restart.message"),
                Component.translatable("yacl.restart.yes"),
                Component.translatable("yacl.restart.no")
        );
    }
}
