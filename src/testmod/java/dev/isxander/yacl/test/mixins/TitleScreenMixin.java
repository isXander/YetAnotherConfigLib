package dev.isxander.yacl.test.mixins;

import dev.isxander.yacl.test.config.GuiTest;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For testing purposes! If you are using this as
 * an example, ignore this class!
 */
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void injectTestButton(CallbackInfo ci) {
        addRenderableWidget(new Button(
                0, 0,
                50, 20,
                Component.literal("YACL"),
                button -> minecraft.setScreen(GuiTest.getModConfigScreenFactory(minecraft.screen))
        ));
    }
}
