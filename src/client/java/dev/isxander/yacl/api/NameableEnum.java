package dev.isxander.yacl.api;

import net.minecraft.network.chat.Component;

/**
 * Used for the default value formatter of {@link dev.isxander.yacl.gui.controllers.cycling.EnumController}
 */
public interface NameableEnum {
    Component getDisplayName();
}
