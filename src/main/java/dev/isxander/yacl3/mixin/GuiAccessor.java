package dev.isxander.yacl3.mixin;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;

//? >=26.2 {
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.gen.Accessor;
//? }

@Mixin(Gui.class)
public interface GuiAccessor {
    //? >=26.2 {
    @Accessor("screen")
    void yacl$setScreen(final Screen screen);
    //? }
}
