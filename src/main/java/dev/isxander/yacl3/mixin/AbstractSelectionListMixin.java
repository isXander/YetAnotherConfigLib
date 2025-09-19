package dev.isxander.yacl3.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>> {
    @Shadow public abstract List<E> children();

    /**
     * Mojang use the field access of children to get max index to loop through keyboard navigation to find the next entry.
     * YACL modifies these children() method to filter out hidden entries, so we need to redirect the field access to the
     * method, so we don't get ArrayIndexOutOfBoundsException.
     */
    @WrapOperation(method = "nextEntry(Lnet/minecraft/client/gui/navigation/ScreenDirection;Ljava/util/function/Predicate;Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/AbstractSelectionList;children:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<E> modifyChildrenCall(AbstractSelectionList<E> instance, Operation<List<E>> original) {
        return children();
    }
}
