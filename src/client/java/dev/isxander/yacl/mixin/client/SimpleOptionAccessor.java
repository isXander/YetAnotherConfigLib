package dev.isxander.yacl.mixin.client;

import net.minecraft.client.option.SimpleOption;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@ApiStatus.Internal
@Mixin(SimpleOption.class)
public interface SimpleOptionAccessor<T> {
    @Accessor
    T getDefaultValue();
}
