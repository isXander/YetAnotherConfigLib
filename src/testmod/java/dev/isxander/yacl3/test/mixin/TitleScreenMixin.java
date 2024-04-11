package dev.isxander.yacl3.test.mixin;

import dev.isxander.yacl3.test.GuiTest;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void addButton(CallbackInfo ci) {
        addRenderableWidget(Button.builder(Component.literal("YetAnotherConfigLib Test"), button -> {
            minecraft.setScreen(GuiTest.getModConfigScreenFactory(minecraft.screen));
        }).width(150).build());
    }
}
