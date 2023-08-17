package dev.isxander.yacl3.mixin;

import dev.isxander.yacl3.gui.image.ImageRendererManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "close", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/telemetry/ClientTelemetryManager;close()V"))
    private void closeImages(CallbackInfo ci) {
        ImageRendererManager.closeAll();
    }

    @Inject(method = "runTick", at = @At(value = "HEAD"))
    private void finaliseImages(boolean tick, CallbackInfo ci) {
        ImageRendererManager.pollImageFactories();
    }
}
