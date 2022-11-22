package dev.isxander.yacl.test.mixins;

import dev.isxander.yacl.test.GuiTest;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void injectTestButton(CallbackInfo ci) {
        addDrawableChild(ButtonWidget.builder(Text.of("YACL"), button -> client.setScreen(GuiTest.getModConfigScreenFactory(client.currentScreen)))
                .position(0, 0)
                .width(50)
                .build());
    }
}
