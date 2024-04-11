package dev.isxander.yacl3.mixin;

import dev.isxander.yacl3.gui.image.ImageRendererManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "destroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;close()V", shift = At.Shift.BEFORE))
    private void closeImages(CallbackInfo ci) {
        ImageRendererManager.closeAll();
    }
}
