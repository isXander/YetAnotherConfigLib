package dev.isxander.yacl.mixin;

import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin {
    /**
     * This mixin is used to prevent the tab bar from being focused when navigating left or right
     * through the YACL options screen. This can also apply to vanilla as navigating left or right
     * should never result in focusing the always-at-the-top tab bar.
     * Without this, navigating right from the option list focuses the tab bar, not the action buttons/description.
     */
    @Redirect(method = {"nextFocusPathVaguelyInDirection", "nextFocusPathInDirection"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/events/ContainerEventHandler;children()Ljava/util/List;"))
    private List<?> modifyFocusCandidates(ContainerEventHandler instance, ScreenRectangle screenArea, ScreenDirection direction, @Nullable GuiEventListener focused, FocusNavigationEvent event) {
        if (direction.getAxis() == ScreenAxis.HORIZONTAL)
            return instance.children().stream().filter(child -> !(child instanceof TabNavigationBar)).toList();
        return instance.children();
    }
}
