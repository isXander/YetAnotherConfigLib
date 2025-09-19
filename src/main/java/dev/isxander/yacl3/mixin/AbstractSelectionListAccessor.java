package dev.isxander.yacl3.mixin;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AbstractSelectionList.class)
public interface AbstractSelectionListAccessor {
    //? if >=1.21.9 {
    @Accessor
    List<?> getChildren();
    //?}

    //? if >=1.21.4 && <1.21.9 {
    /*@Accessor
    void setRenderHeader(boolean render);
    *///?}
}
